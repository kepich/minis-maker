package org.vaadin.example;

import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.router.Route;
import org.vaadin.example.service.MiniaturesService;
import org.vaadin.example.service.PNGBuilderService;
import org.vaadin.example.toolbar.PreviewDetails;
import org.vaadin.example.toolbar.RightPanel;
import org.vaadin.example.toolbar.ToolbarPanel;

@Route
public class MainView extends SplitLayout {
    public MainView(MiniaturesService miniaturesService, PNGBuilderService pngBuilderService) {
        PreviewDetails previewDetails = new PreviewDetails(miniaturesService::selected);
        addToPrimary(new ToolbarPanel(miniaturesService, previewDetails));
        addToSecondary(new RightPanel(pngBuilderService, previewDetails));
        setSplitterPosition(20);
        setHeightFull();
        addThemeVariants(SplitLayoutVariant.LUMO_SMALL);
    }
}
