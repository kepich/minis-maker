package org.vaadin.example;

import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.example.toolbar.RightPanel;
import org.vaadin.example.service.MiniaturesService;
import org.vaadin.example.service.PNGBuilderService;
import org.vaadin.example.toolbar.ToolbarPanel;

@Route
public class MainView extends SplitLayout {

    public MainView(MiniaturesService miniaturesService, PNGBuilderService pngBuilderService) {
        super(new ToolbarPanel(miniaturesService), new RightPanel(pngBuilderService));

        setSplitterPosition(25);
        setHeightFull();
    }
}
