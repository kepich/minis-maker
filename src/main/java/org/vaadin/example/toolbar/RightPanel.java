package org.vaadin.example.toolbar;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.example.service.PNGBuilderService;

import static org.vaadin.example.pdf.PDFUtils.createPDFStreamResource;

public class RightPanel extends SplitLayout {

    public RightPanel(PNGBuilderService pngBuilderService, PreviewDetails previewDetails) {
        addThemeVariants(SplitLayoutVariant.LUMO_SMALL);
        setSplitterPosition(80);

        Image imageViewer = new Image();
        imageViewer.setWidthFull();
        Button generatePng = new Button("Build PDF", e -> {
            imageViewer.setSrc(pngBuilderService.getPagesStreamResource());
        });
        Button downloadPdf = new Button("Download", e -> {
            StreamResource streamResource = createPDFStreamResource(pngBuilderService.getPagePNGBytes());
            StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(streamResource);
            UI.getCurrent().getPage().open(registration.getResourceUri().toString());
        });
        Scroller scroller = new Scroller(imageViewer);
        scroller.setWidthFull();
        VerticalLayout verticalLayout = new VerticalLayout(new HorizontalLayout(generatePng, downloadPdf), previewDetails);
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);
        verticalLayout.setWidth("140px");
        addToPrimary(scroller);
        addToSecondary(verticalLayout);
    }
}
