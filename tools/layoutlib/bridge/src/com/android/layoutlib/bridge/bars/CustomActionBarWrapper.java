/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.layoutlib.bridge.bars;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.android.ide.common.rendering.api.ActionBarCallback;
import com.android.ide.common.rendering.api.ActionBarCallback.HomeButtonStyle;
import com.android.ide.common.rendering.api.RenderResources;
import com.android.ide.common.rendering.api.ResourceValue;
import com.android.ide.common.rendering.api.SessionParams;
import com.android.internal.R;
import com.android.internal.app.ToolbarActionBar;
import com.android.internal.app.WindowDecorActionBar;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.widget.ActionBarAccessor;
import com.android.internal.widget.ActionBarView;
import com.android.internal.widget.DecorToolbar;
import com.android.layoutlib.bridge.android.BridgeContext;
import com.android.layoutlib.bridge.impl.ResourceHelper;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowCallback;
import android.widget.ActionMenuPresenter;
import android.widget.Toolbar;
import android.widget.Toolbar_Accessor;

import static com.android.SdkConstants.ANDROID_NS_NAME_PREFIX;
import static com.android.resources.ResourceType.MENU;

/**
 * A common API to access {@link ToolbarActionBar} and {@link WindowDecorActionBar}.
 */
public abstract class CustomActionBarWrapper {

    @NonNull protected ActionBar mActionBar;
    @NonNull protected SessionParams mParams;
    @NonNull protected ActionBarCallback mCallback;
    @NonNull protected BridgeContext mContext;

    /**
     * Returns a wrapper around different implementations of the Action Bar to provide a common API.
     *
     * @param decorContent the top level view returned by inflating
                     * ?attr/windowActionBarFullscreenDecorLayout
     */
    @NonNull
    public static CustomActionBarWrapper getActionBarWrapper(@NonNull BridgeContext context,
            @NonNull SessionParams params, @NonNull View decorContent) {
        View view = decorContent.findViewById(R.id.action_bar);
        if (view instanceof Toolbar) {
            return new ToolbarWrapper(context, params, ((Toolbar) view)
            );
        } else if (view instanceof ActionBarView) {
            return new WindowActionBarWrapper(context, params, decorContent, ((ActionBarView) view)
            );
        } else {
            throw new IllegalStateException("Can't make an action bar out of " +
                    view.getClass().getSimpleName());
        }
    }

    CustomActionBarWrapper(@NonNull BridgeContext context, @NonNull SessionParams params,
            @NonNull ActionBar actionBar) {
        mActionBar = actionBar;
        mParams = params;
        mCallback = params.getProjectCallback().getActionBarCallback();
        mContext = context;
    }

    protected void setupActionBar() {
        // Do the things that are common to all implementations.
        RenderResources res = mContext.getRenderResources();

        String title = mParams.getAppLabel();
        ResourceValue titleValue = res.findResValue(title, false);
        if (titleValue != null && titleValue.getValue() != null) {
            mActionBar.setTitle(titleValue.getValue());
        } else {
            mActionBar.setTitle(title);
        }

        String subTitle = mCallback.getSubTitle();
        if (subTitle != null) {
            mActionBar.setSubtitle(subTitle);
        }

        // Add show home as up icon.
        if (mCallback.getHomeButtonStyle() == HomeButtonStyle.SHOW_HOME_AS_UP) {
            mActionBar.setDisplayOptions(0xFF, ActionBar.DISPLAY_HOME_AS_UP);
        }
    }

    protected boolean isSplit() {
        return getDecorToolbar().isSplit();
    }

    protected boolean isOverflowPopupNeeded() {
        return mCallback.isOverflowPopupNeeded();
    }

    /**
     * Gets the menus to add to the action bar from the callback, resolves them, inflates them and
     * adds them to the action bar.
     */
    protected void inflateMenus() {
        MenuInflater inflater = new MenuInflater(getActionMenuContext());
        MenuBuilder menuBuilder = getMenuBuilder();
        for (String name : mCallback.getMenuIdNames()) {
            int id;
            if (name.startsWith(ANDROID_NS_NAME_PREFIX)) {
                // Framework menu.
                name = name.substring(ANDROID_NS_NAME_PREFIX.length());
                id = mContext.getFrameworkResourceValue(MENU, name, -1);
            } else {
                // Project menu.
                id = mContext.getProjectResourceValue(MENU, name, -1);
            }
            if (id > -1) {
                inflater.inflate(id, menuBuilder);
            }
        }
    }

    /**
     * The context used for the ActionBar and the menus in the ActionBarView.
     */
    @NonNull
    protected Context getActionMenuContext() {
        return mActionBar.getThemedContext();
    }

    /**
     * The context used to inflate the popup menu.
     */
    @NonNull
    abstract Context getPopupContext();

    /**
     * The Menu in which to inflate the user's menus.
     */
    @NonNull
    abstract MenuBuilder getMenuBuilder();

    @Nullable
    abstract ActionMenuPresenter getActionMenuPresenter();

    /**
     * Framework's wrapper over two ActionBar implementations.
     */
    @NonNull
    abstract DecorToolbar getDecorToolbar();

    // ---- The implementations ----

    /**
     * Material theme uses {@link Toolbar} as the action bar. This wrapper provides access to
     * Toolbar using a common API.
     */
    private static class ToolbarWrapper extends CustomActionBarWrapper {

        @NonNull
        private final Toolbar mToolbar;  // This is the view.

        ToolbarWrapper(@NonNull BridgeContext context, @NonNull SessionParams params,
                @NonNull Toolbar toolbar) {
            super(context, params, new ToolbarActionBar(toolbar, "", new WindowCallback())
            );
            mToolbar = toolbar;
        }

        @Override
        protected void inflateMenus() {
            super.inflateMenus();
            // Inflating the menus doesn't initialize the ActionMenuPresenter. Setting a fake menu
            // and then setting it back does the trick.
            MenuBuilder menu = getMenuBuilder();
            DecorToolbar decorToolbar = getDecorToolbar();
            decorToolbar.setMenu(new MenuBuilder(getActionMenuContext()), null);
            decorToolbar.setMenu(menu, null);
        }

        @NonNull
        @Override
        Context getPopupContext() {
            return Toolbar_Accessor.getPopupContext(mToolbar);
        }

        @NonNull
        @Override
        MenuBuilder getMenuBuilder() {
            return (MenuBuilder) mToolbar.getMenu();
        }

        @Nullable
        @Override
        ActionMenuPresenter getActionMenuPresenter() {
            return Toolbar_Accessor.getActionMenuPresenter(mToolbar);
        }

        @NonNull
        @Override
        DecorToolbar getDecorToolbar() {
            return mToolbar.getWrapper();
        }
    }

    /**
     * Holo theme uses {@link WindowDecorActionBar} as the action bar. This wrapper provides
     * access to it using a common API.
     */
    private static class WindowActionBarWrapper extends CustomActionBarWrapper{

        @NonNull
        private final WindowDecorActionBar mActionBar;
        private final ActionBarView mActionBarView;
        private MenuBuilder mMenuBuilder;

        public WindowActionBarWrapper(@NonNull BridgeContext context, @NonNull SessionParams params,
                @NonNull View decorContentRoot, @NonNull ActionBarView actionBarView) {
            super(context, params, new WindowDecorActionBar(decorContentRoot)
            );
            mActionBarView = actionBarView;
            mActionBar = ((WindowDecorActionBar) super.mActionBar);
        }

        @Override
        protected void setupActionBar() {
            super.setupActionBar();

            // Set the navigation mode.
            int navMode = mCallback.getNavigationMode();
            mActionBar.setNavigationMode(navMode);
            //noinspection deprecation
            if (navMode == ActionBar.NAVIGATION_MODE_TABS) {
                setupTabs(3);
            }

            String icon = mParams.getAppIcon();
            // If the action bar style doesn't specify an icon, set the icon obtained from the
            // session params.
            if (!mActionBar.hasIcon() && icon != null) {
                Drawable iconDrawable = getDrawable(icon, false);
                if (iconDrawable != null) {
                    mActionBar.setIcon(iconDrawable);
                }
            }

            // Set action bar to be split, if needed.
            ViewGroup splitView = (ViewGroup) mActionBarView.findViewById(R.id.split_action_bar);
            if (splitView != null) {
                mActionBarView.setSplitView(splitView);
                Resources res = mContext.getResources();
                boolean split = res.getBoolean(R.bool.split_action_bar_is_narrow)
                        && mCallback.getSplitActionBarWhenNarrow();
                mActionBarView.setSplitToolbar(split);
            }
        }

        @Override
        protected void inflateMenus() {
            super.inflateMenus();
            // The super implementation doesn't set the menu on the view. Set it here.
            mActionBarView.setMenu(getMenuBuilder(), null);
        }

        @NonNull
        @Override
        Context getPopupContext() {
            return getActionMenuContext();
        }

        @NonNull
        @Override
        MenuBuilder getMenuBuilder() {
            if (mMenuBuilder == null) {
                mMenuBuilder = new MenuBuilder(getActionMenuContext());
            }
            return mMenuBuilder;
        }

        @Nullable
        @Override
        ActionMenuPresenter getActionMenuPresenter() {
            return ActionBarAccessor.getActionMenuPresenter(mActionBarView);
        }

        @NonNull
        @Override
        ActionBarView getDecorToolbar() {
            return mActionBarView;
        }

        // TODO: Use an adapter, like List View to set up tabs.
        @SuppressWarnings("deprecation")  // For Tab
        private void setupTabs(int num) {
            for (int i = 1; i <= num; i++) {
                Tab tab = mActionBar.newTab().setText("Tab" + i).setTabListener(new TabListener() {
                    @Override
                    public void onTabUnselected(Tab t, FragmentTransaction ft) {
                        // pass
                    }
                    @Override
                    public void onTabSelected(Tab t, FragmentTransaction ft) {
                        // pass
                    }
                    @Override
                    public void onTabReselected(Tab t, FragmentTransaction ft) {
                        // pass
                    }
                });
                mActionBar.addTab(tab);
            }
        }

        @Nullable
        private Drawable getDrawable(@NonNull String name, boolean isFramework) {
            RenderResources res = mContext.getRenderResources();
            ResourceValue value = res.findResValue(name, isFramework);
            value = res.resolveResValue(value);
            if (value != null) {
                return ResourceHelper.getDrawable(value, mContext);
            }
            return null;
        }

    }
}