package org.vaadin.example;

import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.example.pdf.RightPanel;
import org.vaadin.example.service.MiniaturesService;
import org.vaadin.example.toolbar.ToolbarPanel;

import java.io.IOException;

@Route
public class MainView extends SplitLayout {

    public MainView(MiniaturesService miniaturesService) throws IOException {
        super(new ToolbarPanel(miniaturesService), new RightPanel(miniaturesService));

        setSplitterPosition(25);
        setHeightFull();
    }
}
