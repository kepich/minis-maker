package org.vaadin.example.toolbar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import org.vaadin.example.Miniature;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

public class PreviewDetails extends Details implements Switchable {
    private final Image preview = new Image();
    private final Span filename = new Span ("File name");
    private final IntegerField numberField = new IntegerField("Number of models");

    public PreviewDetails(Supplier<Optional<Miniature>> miniatureSupplier) {
        super("Preview");

        disable();
        applyStyles();
        setWidthFull();

        numberField.addValueChangeListener(
                e -> miniatureSupplier.get().ifPresent(m -> m.setNumber(e.getValue())));

        add(getPreviewPanel());
    }

    private void applyStyles() {
        addThemeVariants(DetailsVariant.SMALL, DetailsVariant.FILLED);

        numberField.setWidth("160px");
        numberField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
    }

    private Component getPreviewPanel() {
        numberField.setMin(1);
        numberField.setValue(1);
        numberField.setStepButtonsVisible(true);
        VerticalLayout verticalLayout = new VerticalLayout(filename, preview, numberField);
        verticalLayout.setSpacing(false);
        return verticalLayout;
    }

    public void update(Miniature miniature) {
        filename.setText(miniature.getFileName());
        numberField.setValue(miniature.getNumber());
        try {
            preview.setSrc(miniature.getCroppedStreamResource());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        preview.setWidthFull();
    }

    @Override
    public void enable() {
        setEnabled(true);
    }

    @Override
    public void disable() {
        setEnabled(false);
        setOpened(false);
    }
}
