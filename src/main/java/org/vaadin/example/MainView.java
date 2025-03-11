package org.vaadin.example;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.example.pdf.PdfViewer;
import org.vaadin.example.service.MiniaturesService;

@Route
public class MainView extends SplitLayout {

    public MainView(MiniaturesService miniaturesService) {
        super(new ToolbarPanel(miniaturesService), new PdfViewer("OPR Army Forge.pdf", miniaturesService));
        setSplitterPosition(25);
        setHeightFull();
    }
}
