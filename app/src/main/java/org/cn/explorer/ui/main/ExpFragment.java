package org.cn.explorer.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.cn.explorer.MainActivity;
import org.cn.explorer.R;
import org.cn.explorer.common.AppConfig;
import org.cn.explorer.common.Const;
import org.cn.explorer.listener.AppDialogListener;
import org.cn.explorer.model.lazyloader.ImageLoader;
import org.cn.explorer.model.lazyloader.Item;
import org.cn.explorer.service.main.ExpListener;
import org.cn.explorer.service.main.ExpService;
import org.cn.explorer.ui.BaseFragment;
import org.cn.explorer.utils.DialogUtil;
import org.cn.explorer.utils.FileUtil;
import org.cn.explorer.vo.ExpItem;
import org.cn.utils.AppUtil;

import java.io.File;
import java.io.Serializable;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by chenning on 2015/10/10.
 */
public class ExpFragment extends BaseFragment implements ActionMode.Callback {

    private RecyclerView mRecyclerView;
    private ExpAdapter mExpAdapter;

    private String path;

    private Stack<String> stack = new Stack<>();

    private List<ExpItem> checkedList = new ArrayList<>();

    private OnOpenFolderListener mOnOpenFolderListener;

    private ImageLoader loader;

    private Toolbar mToolbar;
    private ActionMode mActionMode;

    private View loadingView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            savedInstanceState = getArguments();
        }
        if (savedInstanceState != null) {
            path = savedInstanceState.getString(Const.ROOT_PATH);
            Stack<String> tmp = (Stack<String>) savedInstanceState.getSerializable("stack");
            if (tmp != null && !tmp.isEmpty()) {
                stack = tmp;
                path = stack.peek();
            }
            if (stack.isEmpty() && path != null) {
                stack.push(path);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Const.ROOT_PATH, path);
        outState.putSerializable("stack", stack);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explorer, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!stack.isEmpty()) {
            loadData(new File(stack.peek()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setVerticalScrollBarEnabled(true);

        mExpAdapter = new ExpAdapter(getActivity());
        mRecyclerView.setAdapter(mExpAdapter);

        initData();
    }

    private void initData() {
        loader = ImageLoader.newInstance(getActivity(), new File(dataManager.getRootDirectory() + File.separator + AppConfig.cache + File.separator));
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (args != null) {
            path = args.getString(Const.ROOT_PATH);
        }
    }

    public void reload() {
        if (path == null) {
            return;
        }
        loadData(new File(path));
    }

    private void loadData(File file) {
        if (file == null) {
            return;
        }

        if (!file.getPath().equals(path)) {
            path = file.getPath();
            stack.push(path);
        }
        AppConfig.currentFilePath = file;

        loading(true);
        ExpService.async(file, new ExpListener() {
            @Override
            public void onResponse(List<ExpItem> items) {
                try {
                    if (items != null && mExpAdapter != null) {
                        mExpAdapter.updateData(items);
                    }
                } finally {
                    loading(false);
                }
            }
        });
    }

    public void loading(boolean isLoading) {
        if (loadingView != null) {
            loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onItemClick(ExpItem item) {
        try {
            if (item == null) {
                return;
            }
            Log.d("EF", "" + item.toString());

            if (mActionMode != null) {
                onItemLongClick(item);
            } else {
                if (item.getPermission() != null && item.getPermission().length() > 3 && !"r".equals(item.getPermission().substring(1, 2))) {
                    Snackbar.make(mToolbar, "permission denied", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    return;
                }
                if (item.getFile().isDirectory()) {
                    // loadData(item.getFile());
                    if (mOnOpenFolderListener != null) {
                        mOnOpenFolderListener.onOpenFolder(item.getFile().getPath());
                    }
                } else if (item.getFile().isFile()) {
                    item.setContentType(URLConnection.getFileNameMap().getContentTypeFor(item.getFile().getPath()));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(item.getFile()), (TextUtils.isEmpty(item.getContentType()) ? "*/*" : item.getContentType()));
                    getActivity().startActivity(intent);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public boolean onItemLongClick(ExpItem item) {
        try {
            if (item == null) {
                return false;
            }

            if (item.isChecked() && checkedList.contains(item)) {
                checkedList.remove(item);
                item.setIsChecked(false);
            } else if (!checkedList.contains(item)) {
                item.setIsChecked(true);
                checkedList.add(item);
            }

            if (mActionMode == null) {
                ((AppCompatActivity) getActivity()).startSupportActionMode(ExpFragment.this);
            }
            if (mActionMode != null) {
                mActionMode.setTitle("" + checkedList.size());
                if (checkedList.size() == mExpAdapter.getItemCount()) {
                    mActionMode.setTitle("All");
                }
                if (checkedList.isEmpty()) {
                    mActionMode.finish();
                }
            }

            mExpAdapter.notifyItemChanged(item.getPosition());
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean onBackPressed() {
        if (checkedList != null && checkedList.size() > 0) {
            clearSelected();
            return true;
        }

        if (stack.size() > 1) {
            stack.pop();
            loadData(new File(stack.pop()));
            return true;
        } else if (!stack.isEmpty()) {
            loadData(new File(stack.pop()));
            return true;
        }
        return false;
    }

    public void clearSelected() {
        if (checkedList != null && checkedList.size() > 0) {
            for (int i = 0; i < checkedList.size(); i++) {
                checkedList.get(i).setIsChecked(false);
                mExpAdapter.notifyItemChanged(checkedList.get(i).getPosition());
            }
            checkedList.clear();
        }
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnOpenFolderListener) {
            // this.mOnOpenFolderListener = (OnOpenFolderListener) context;
        }
        if (context instanceof MainActivity) {
            this.loadingView = ((MainActivity) context).getLoading();
            this.mToolbar = ((MainActivity) context).getToolbar();
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        this.mOnOpenFolderListener = null;
        clearSelected();
        super.onDetach();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        this.mActionMode = mode;
        mode.getMenuInflater().inflate(R.menu.action_mode_explorer, menu);

        AppUtil.setOptionalIconsVisible(menu, true);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        if (!checkedList.isEmpty() && checkedList.size() != 1) {
            menu.setGroupVisible(R.id.one_item_only, false);
        } else if (!checkedList.isEmpty()) {
            menu.setGroupVisible(R.id.one_item_only, true);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Snackbar.make(mToolbar, "action_share", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case R.id.action_delete:
                if (checkedList != null && !checkedList.isEmpty()) {
                    DialogUtil.alertDialog(getActivity(), getString(R.string.action_menu_delete), getString(R.string.msg_delete_file), getString(R.string.action_cancel), getString(R.string.action_confirm), null, new AppDialogListener() {
                        @Override
                        public void onPositiveButtonClickListener() {
                            super.onPositiveButtonClickListener();
                            try {
                                for (ExpItem expItem : checkedList) {
                                    FileUtil.deleteFile(expItem.getFile());
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            } finally {
                                clearSelected();
                                reload();
                            }
                        }
                    });
                }
                break;
            case R.id.action_copy:
                Snackbar.make(mToolbar, "action_copy", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case R.id.action_cut:
                Snackbar.make(mToolbar, "action_cut", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case R.id.action_information:
                if (checkedList.isEmpty() || checkedList.size() != 1) {
                    Snackbar.make(mToolbar, "one item selected only.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    break;
                }
                final View infoView = View.inflate(getActivity(), R.layout.layout_file_information, null);
                ((TextView) infoView.findViewById(R.id.item_size)).setText(getString(R.string.size) + ": " + checkedList.get(0).getSize());
                if (checkedList.get(0).getFile().isDirectory()) {
                    ((TextView) infoView.findViewById(R.id.item_content_type)).setText(getString(R.string.content) + ": " + checkedList.get(0).getChildrenCount());
                } else {
                    ((TextView) infoView.findViewById(R.id.item_content_type)).setText(getString(R.string.content_type) + ": " + URLConnection.getFileNameMap().getContentTypeFor(checkedList.get(0).getFile().getPath()));
                }
                ((TextView) infoView.findViewById(R.id.item_permission)).setText(getString(R.string.permission) + ": " + checkedList.get(0).getPermission());
                ((TextView) infoView.findViewById(R.id.item_modified)).setText(getString(R.string.modified) + ": " + checkedList.get(0).getLastModified());
                DialogUtil.alertDialog(getActivity(), checkedList.get(0).getTitle(), "", null, getString(R.string.action_confirm), infoView, null);
                break;
            case R.id.action_rename:
                if (checkedList.isEmpty() || checkedList.size() != 1) {
                    Snackbar.make(mToolbar, "one item selected only.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    break;
                }
                final File beforeRenameFile = checkedList.get(0).getFile();
                final View renameView = View.inflate(getActivity(), R.layout.layout_edit_text, null);
                final EditText rename = (EditText) renameView.findViewById(R.id.edit_text_view);
                rename.setText(beforeRenameFile.getName());
                rename.selectAll();
                AppUtil.showKeyboard(getActivity(), rename);
                DialogUtil.alertDialog(getActivity(), getString(R.string.action_menu_rename), "", getString(R.string.action_cancel), getString(R.string.action_confirm), renameView, new AppDialogListener() {
                    @Override
                    public void onPositiveButtonClickListener() {
                        super.onPositiveButtonClickListener();
                        try {
                            String result = rename.getText().toString();
                            if (TextUtils.isEmpty(result)) {
                                Snackbar.make(mToolbar, "input content please.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                return;
                            }
                            beforeRenameFile.renameTo(new File(path + File.separator + result));
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            clearSelected();
                            reload();
                        }
                    }
                });
                break;
            default:
                Snackbar.make(mToolbar, "unknown action", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                break;
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mode.getMenu().clear();
        mActionMode = null;
        clearSelected();
        // mExpAdapter.notifyDataSetChanged();
    }

    class ExpAdapter extends RecyclerView.Adapter<ExpAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            private View view;
            public ImageView icon;
            public TextView title, subtitle, modified, permission;

            private ExpItem item;

            public ViewHolder(View itemView) {
                super(itemView);
                this.view = itemView;
                this.icon = (ImageView) itemView.findViewById(R.id.item_icon);
                this.title = (TextView) itemView.findViewById(R.id.item_title);
                this.subtitle = (TextView) itemView.findViewById(R.id.item_subtitle);
                this.modified = (TextView) itemView.findViewById(R.id.item_modified);
                this.permission = (TextView) itemView.findViewById(R.id.item_permission);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            public void onBindView(ExpItem item) {
                this.item = item;
                this.title.setText(item.getTitle());
                this.modified.setText(item.getLastModified());
                this.permission.setText(item.getPermission());

                if (item.getFile().isDirectory()) {
                    this.subtitle.setText(item.getChildrenCount() + "项");
                } else {
                    this.subtitle.setText(item.getSize());
                }
                if (item.getTitle().startsWith(".")) {
                    this.title.setTextColor(Color.argb(0xFF, 0xA0, 0xA0, 0xA0));
                } else {
                    this.title.setTextColor(Color.argb(0xFF, 0x00, 0x00, 0x00));
                }
                if (item.isChecked()) {
                    view.setBackgroundColor(Color.argb(0xFF, 0xF0, 0xF0, 0xF0));
                } else {
                    view.setBackgroundColor(Color.argb(0xFF, 0xFF, 0xFF, 0xFF));
                }

                int iconId = R.drawable.ic_folder;
                if (item.getFile().isFile()) {
                    iconId = R.drawable.ic_unknow_small;
                    if (!TextUtils.isEmpty(item.getContentType())) {
                        if (item.getContentType().contains("image")) {
                            iconId = R.drawable.ic_photo_small;
                        } else if (item.getContentType().contains("video")) {
                            iconId = R.drawable.ic_movie_small;
                        } else if (item.getContentType().contains("audio")) {
                            iconId = R.drawable.ic_music_small;
                        } else if (item.getContentType().contains("text")) {
                            iconId = R.drawable.ic_txt_small;
                        } else if (item.getContentType().contains("pdf")) {
                            iconId = R.drawable.ic_pdf_small;
                        } else if (item.getContentType().contains("android.package")) {
                            iconId = R.drawable.ic_unknow_small;
                        }
                    }
                }
                loader.display(new Item(item.getFile().getPath(), this.icon, item.getFile(), TextUtils.isEmpty(item.getContentType()) ? "*/*" : item.getContentType(), iconId));
            }

            @Override
            public void onClick(View v) {
                Log.d("EF", "onClick: " + this.item.toString());
                onItemClick(this.item);
            }

            @Override
            public boolean onLongClick(View v) {
                Log.d("EF", "onLongClick: " + this.item.toString());
                onItemLongClick(this.item);
                return true;
            }
        }

        private Context ctx;
        private List<ExpItem> items;

        public ExpAdapter(Context ctx) {
            this.ctx = ctx;
        }

        public void updateData(List<ExpItem> items) {
            if (this.items == null) {
                this.items = new ArrayList<>();
            }
            this.items.clear();
            this.items.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public ExpAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ctx).inflate(R.layout.item_explorer_list, parent, false);
            return new ExpAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ExpAdapter.ViewHolder holder, int position) {
            items.get(position).setPosition(position);
            holder.onBindView(items.get(position));
        }

        @Override
        public int getItemCount() {
            return (items == null || items.size() <= 0) ? 0 : items.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public interface OnOpenFolderListener {
        void onOpenFolder(String path);
    }

    public void setOnOpenFolderListener(OnOpenFolderListener mOnOpenFolderListener) {
        this.mOnOpenFolderListener = mOnOpenFolderListener;
    }

}
