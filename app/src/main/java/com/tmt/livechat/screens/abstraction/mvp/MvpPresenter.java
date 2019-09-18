package com.tmt.livechat.screens.abstraction.mvp;

import java.lang.ref.WeakReference;

/**
 * Created by mohammednabil on 2019-06-26.
 */
public abstract class MvpPresenter<T extends MvpInterface.View> implements MvpInterface.Presenter {

    private WeakReference<T> view;

    /**
     **/
    public T view() {
        return view.get();
    }

    /**
     **/
    public void setView(T view) {
        this.view = new WeakReference<>(view);
    }

    /**
     *
     * @return true if view is alive
     */
    protected boolean alive() {
        return view != null;
    }

}
