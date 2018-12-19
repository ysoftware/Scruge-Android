package com.scruge.scruge.dependencies.navigation;

import android.transition.Slide;
import android.view.Gravity;
import android.view.View;

import com.scruge.scruge.view.views.NavigationBar;

import org.jetbrains.annotations.NotNull;

import java.util.Stack;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class NavigationController implements NavigationBar.Delegate {

    private enum Update { will, did }

    /// return true if handled, false to pass further
    public interface OnBackPressedListener {
        boolean onBackPressedHandled();
    }

    // FRAGMENT

    private boolean shouldHideNavigationBar = true;
    private NavigationBar navigationBar;
    private FragmentManager manager;
    private Stack<Fragment> fragmentStack = new Stack<>();
    private Fragment currentFragment;
    private int containerId;
    public boolean isVisible = true;

    public NavigationController(FragmentManager fragmentManager, int containerId) {
        this.containerId = containerId;
        this.manager = fragmentManager;
    }

    public int getContainerId() {
        return containerId;
    }

    public void replaceWith(final Fragment fragment) {
        final Fragment oldFragment = currentFragment;
        currentFragment = fragment;

        manager.executePendingTransactions();
        fragmentStack = new Stack<>();
        manager.popBackStackImmediate(null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss();

        if (fragment instanceof NavigationFragment) {
            ((NavigationFragment) fragment).setNavigationController(this);
        }

        update(oldFragment, fragment, Update.will);
        manager.executePendingTransactions();
        update(oldFragment, fragment, Update.did);

        setBackButtonHidden(true);
    }

    public void navigateTo(final Fragment fragment) {
        final Fragment oldFragment = currentFragment;
        if (android.os.Build.VERSION.SDK_INT > 21) {
            fragment.setEnterTransition(new Slide(Gravity.END));
            fragment.setExitTransition(new Slide(Gravity.START));
        }

        if (currentFragment == null) { return; }

        manager.executePendingTransactions();
        fragmentStack.push(fragment);
        resetClickable();
        manager.beginTransaction()
                .add(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();

        if (fragment instanceof NavigationFragment) {
            ((NavigationFragment) fragment).setNavigationController(this);
        }

        update(oldFragment, fragment, Update.will);
        manager.executePendingTransactions();
        update(oldFragment, fragment, Update.did);

        setBackButtonHidden(false);
    }

    public boolean navigateBack() {
        final Fragment oldFragment = currentFragment;
        if (fragmentStack.size() > 0) {
            fragmentStack.pop();
        }

        Fragment fragment = topFragment();
        if (fragment == null || manager.getBackStackEntryCount() == 0) {
            return false;
        }

        manager.executePendingTransactions();
        update(oldFragment, fragment, Update.will);
        manager.popBackStackImmediate(null, 0);
        update(oldFragment, fragment, Update.did);

        setBackButtonHidden(fragmentStack.size() <= 1);
        return true;
    }

    public boolean onBackPressed() {
        Fragment fr = topFragment();
        if (fr instanceof OnBackPressedListener) {
            return ((OnBackPressedListener) fr).onBackPressedHandled();
        }
        else {
            return navigateBack();
        }
    }

    public boolean isPresenting() {
        return topFragment() != null;
    }

    @Nullable
    public Fragment topFragment() {
        if (fragmentStack.size() == 0) {
            return currentFragment;
        }
        return fragmentStack.peek();
    }

    private void resetClickable() {
        Fragment top = topFragment();
        for (Fragment s : fragmentStack) {
            boolean clickable = false;
            if (s == top) {
                clickable = true;
            }
            View v = s.getView();
            if (v != null) {
                v.setClickable(clickable);
            }
        }
    }

    // NAVIGATION BAR

    @Nullable
    public NavigationBar getNavigationBar() {
        return navigationBar;
    }

    public void setNavigationBar(NavigationBar navigationBar) {
        this.navigationBar = navigationBar;
        navigationBar.setDelegate(this);
        navigationBar.setBackButtonHidden(!shouldHideNavigationBar);
    }

    private void setBackButtonHidden(boolean value) {
        shouldHideNavigationBar = value;
        if (navigationBar != null) {
            navigationBar.setBackButtonHidden(value);
        }
    }

    // HELPER

    private void update(Fragment oldFragment, Fragment fragment, Update update) {
        if (!isVisible) { return; }

        if (update == Update.will) {
            if (fragment instanceof NavigationFragment) {
                ((NavigationFragment) fragment).viewWillAppear();
            }

            if (oldFragment instanceof NavigationFragment) {
                ((NavigationFragment) oldFragment).viewWillDisappear();
            }
        }
        else {
            if (fragment instanceof NavigationFragment) {
                ((NavigationFragment) fragment).viewDidAppear();
            }
            else {
                fragment.onResume();
            }

            if (oldFragment instanceof NavigationFragment) {
                ((NavigationFragment) oldFragment).viewDidDisappear();
            }
            else if (oldFragment != null) {
                oldFragment.onPause();
            }
        }
    }

    @Override public void navigationBarDidClickBackButton(@NotNull NavigationBar instance) {
        navigateBack();
    }
}
