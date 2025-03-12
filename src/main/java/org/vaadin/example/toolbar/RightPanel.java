package org.vaadin.example.toolbar;

import com.itextpdf.text.DocumentException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.example.service.PNGBuilderService;

import java.io.IOException;

import static org.vaadin.example.pdf.PDFUtils.createPDFStreamResource;

public class RightPanel extends HorizontalLayout {

    public RightPanel(PNGBuilderService pngBuilderService) {
        Image imageViewer = new Image();
        imageViewer.setWidthFull();
        Button generatePng = new Button("Generate PNG", e -> {
            imageViewer.setSrc(pngBuilderService.getPagePNGStreamResource());
        });
        Button downloadPdf = new Button("Download PNG", e -> {
            try {
                StreamResource streamResource = createPDFStreamResource(pngBuilderService.getPagePNGBytes());
                StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(streamResource);
                UI.getCurrent().getPage().open(registration.getResourceUri().toString());
            } catch (IOException | DocumentException ex) {
                throw new RuntimeException(ex);
            }
        });
        Scroller scroller = new Scroller(imageViewer);
        scroller.setWidthFull();
        VerticalLayout verticalLayout = new VerticalLayout(generatePng, downloadPdf);
        verticalLayout.setAlignItems(Alignment.END);
        verticalLayout.setWidth("140px");
        add(scroller, verticalLayout);
    }
}
