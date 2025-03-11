package org.vaadin.example;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.vaadin.example.service.MiniaturesService;
import org.vaadin.example.toolbar.BaseDetails;
import org.vaadin.example.toolbar.PaddingDetails;
import org.vaadin.example.toolbar.PreviewDetails;

import java.io.IOException;

public class ToolbarPanel extends VerticalLayout {
    private final MiniaturesService miniaturesService;

    private final ListBox<Miniature> filesListBox = new ListBox<>();
    private final BaseDetails baseDetailsPanel = new BaseDetails();
    private final PreviewDetails previewPanel = new PreviewDetails();
    private final PaddingDetails paddingImagePanel;

    public ToolbarPanel(MiniaturesService miniaturesService) {
        this.miniaturesService = miniaturesService;
        this.paddingImagePanel = new PaddingDetails(miniaturesService::selected, previewPanel::update);
        Details uploadImagePanel = new Details("Library", getUploadImagePanel());
        uploadImagePanel.addThemeVariants(DetailsVariant.SMALL, DetailsVariant.FILLED);
        uploadImagePanel.setWidthFull();
        setSpacing(false);
        add(uploadImagePanel, baseDetailsPanel, paddingImagePanel, previewPanel);
    }

    private Component getUploadImagePanel() {
        filesListBox.setRenderer(new ComponentRenderer<Component, Miniature>(miniature -> new Span(miniature.getFileName())));

        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = getUpload(buffer);

        filesListBox.addValueChangeListener(e -> {
            miniaturesService.select(e.getValue()).ifPresentOrElse(miniature -> {
                baseDetailsPanel.enable();
                paddingImagePanel.enable();
                paddingImagePanel.setPaddings(
                    miniature.getPaddingTop(),
                    miniature.getPaddingBottom(),
                    miniature.getPaddingLeft(),
                    miniature.getPaddingRight());
                previewPanel.enable();
                previewPanel.update(miniature);
            }, () -> {
                baseDetailsPanel.disable();
                paddingImagePanel.disable();
                previewPanel.disable();
            });
        });

        return new VerticalLayout(filesListBox, upload);
    }

    private Upload getUpload(MultiFileMemoryBuffer buffer) {
        Upload upload = new Upload(buffer);

        upload.addSucceededListener(event -> {
            try {
                String fileName = event.getFileName();
                filesListBox.setItems(miniaturesService.addAndSelect(new Miniature(fileName, buffer.getInputStream(fileName))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        upload.addFileRemovedListener(event -> {
            String fileName = event.getFileName();
            filesListBox.setItems(miniaturesService.remove(fileName));
        });
        return upload;
    }
}
