package com.wbrawner.simplemarkdown.utility;

import android.text.Editable;

import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.wbrawner.simplemarkdown.presentation.MarkdownPresenter;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MarkdownObserver implements Observer<TextViewAfterTextChangeEvent> {
    private MarkdownPresenter presenter;
    private Observable<TextViewAfterTextChangeEvent> obs;

    public MarkdownObserver(MarkdownPresenter presenter, Observable<TextViewAfterTextChangeEvent> obs) {
        this.presenter = presenter;
        this.obs = obs;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(TextViewAfterTextChangeEvent event) {
        Editable editable = event.editable();
        if (editable == null) {
            return;
        }
        presenter.onMarkdownEdited(editable.toString());
    }

    @Override
    public void onError(Throwable e) {
        System.err.println("An error occurred while handling the markdown");
        e.printStackTrace();
        // TODO: report this?
        obs.subscribe(this);
    }

    @Override
    public void onComplete() {

    }
}
