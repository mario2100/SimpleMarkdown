package com.wbrawner.simplemarkdown;

import com.wbrawner.simplemarkdown.model.MarkdownFile;
import com.wbrawner.simplemarkdown.presentation.MarkdownPresenter;
import com.wbrawner.simplemarkdown.view.MarkdownEditView;
import com.wbrawner.simplemarkdown.view.MarkdownPreviewView;

import org.junit.Before;

import static org.mockito.Mockito.mock;

public class MarkdownPresenterTests {

    MarkdownFile mockMarkdownFile;
    MarkdownPresenter mockPresenter;
    MarkdownEditView editView;
    MarkdownPreviewView previewView;

    @Before
    public void setup() {
        mockMarkdownFile = mock(MarkdownFile.class);
    }
}
