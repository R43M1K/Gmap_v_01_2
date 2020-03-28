package com.example.gmap_v_01_2.presenter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public final class FragmentNavigationController {
    private FragmentNavigationController() {}

    public static void removeFragment(Fragment fragment, FragmentManager fragmentManager) {
        fragmentManager.beginTransaction().remove(fragment).commit();
    }

    public static void removeFragmentPopBackStackByTag(Fragment fragment, FragmentManager fragmentManager, String name, int flags) {
        fragmentManager.beginTransaction().remove(fragment).commit();
        fragmentManager.popBackStack(name, flags);
    }
    
    public static void removeFragmentPopBackStack(Fragment fragment, FragmentManager fragmentManager) {
        fragmentManager.beginTransaction().remove(fragment).commit();
        fragmentManager.popBackStack();
    }

    public static void replaceFragment(int container, Fragment fragment, FragmentManager fragmentManager) {
        fragmentManager.beginTransaction().replace(container, fragment).commit();
    }

    public static void addFragment(int container, Fragment fragment, String name, FragmentManager fragmentManager) {
        fragmentManager.beginTransaction().add(container, fragment).addToBackStack(name).commit();
    }

    public static void addFragmentWithAnimation(int container, Fragment fragment, String name, int animIn, int animOut ,FragmentManager fragmentManager) {
        fragmentManager.beginTransaction().add(container, fragment).addToBackStack(name).setCustomAnimations(animIn, animOut).commit();
    }
}
