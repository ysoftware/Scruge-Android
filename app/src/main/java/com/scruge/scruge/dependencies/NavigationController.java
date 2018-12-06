package com.scruge.scruge.dependencies;

import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import java.util.Stack;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class NavigationController {

    /// return true if handled, false to pass further
    public interface OnBackPressedListener {
        boolean onBackPressedHandled();
    }

    // FRAGMENT

    private FragmentManager manager;
    private Stack<Fragment> fragmentStack = new Stack<>();
    private Fragment currentFragment;
    private int containerId;

    public NavigationController(FragmentManager fragmentManager, int containerId) {
        this.containerId = containerId;
        this.manager = fragmentManager;
    }

    public void replaceWith(Fragment fragment) {
        currentFragment = fragment;
        manager.popBackStackImmediate(null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.beginTransaction()
                .replace(containerId, fragment).commitAllowingStateLoss();
        fragmentStack = new Stack<>();
    }

    public void navigateTo(Fragment fragment) {
        if (android.os.Build.VERSION.SDK_INT > 21) {
            fragment.setEnterTransition(new Slide(Gravity.END));
            fragment.setExitTransition(new Slide(Gravity.START));
        }

        if (currentFragment != null) {
            manager.beginTransaction()
                    .add(containerId, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
            fragmentStack.push(fragment);
            resetClickable();
        }
    }

    public boolean navigateBack() {
        if (fragmentStack.size() > 0) {
            fragmentStack.pop();
        }
        Fragment fr = topFragment();
        if (fr != null) {
            if (manager.getBackStackEntryCount() > 0) {
                manager.popBackStackImmediate(null, 0);
                fr.onResume();
                return true;
            }
        }
        return false;
    }

    public void removeFragment() {
        if (currentFragment != null) {
            manager.beginTransaction()
                    .remove(currentFragment).commitAllowingStateLoss();
            currentFragment = null;
        }
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
}
