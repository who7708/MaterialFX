/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.demo.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import io.github.palexdev.materialfx.controls.MFXCheckListView;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.cell.MFXCheckListCell;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListCell;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListView;
import io.github.palexdev.materialfx.demo.model.Model;
import io.github.palexdev.materialfx.demo.model.Person;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import io.github.palexdev.mfxeffects.enums.ElevationLevel;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.virtualizedfx.cells.base.VFXCell;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.util.StringConverter;

public class ListViewsController implements Initializable {

    @FXML
    private MFXListView<String, VFXCell<String>> list;

    @FXML
    private MFXListView<Person, VFXCell<Person>> custList;

    @FXML
    private MFXCheckListView<String, VFXCell<String>> checkList;

    @FXML
    private MFXLegacyListView<Person> legacyList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> strings = Model.strings;
        ObservableList<Person> people = Model.people;
        StringConverter<Person> converter = FunctionalStringConverter.to(person -> (person == null) ? "" : person.getName() + " " + person.getSurname());

        list.setItems(strings);
        list.setCellFactory(MFXListCell::new);

        custList.setItems(people);
        custList.setCellFactory(p -> new PersonCellFactory(p).setConverter(converter));

        checkList.setItems(strings);
        checkList.setCellFactory(MFXCheckListCell::new);

        legacyList.setItems(people);
        legacyList.setCellFactory(p -> new MFXLegacyListCell<>() {
            @Override
            protected void updateItem(Person item, boolean empty) {
                super.updateItem(item, empty);
                setText(item != null ? converter.toString(item) : "");
            }
        });
    }

    @FXML
    void changeDepth(ActionEvent event) {
        ElevationLevel depth = list.getDepth();
        ElevationLevel next = depth == ElevationLevel.LEVEL0 ? ElevationLevel.LEVEL3 : ElevationLevel.LEVEL0;

        list.setDepth(next);
        custList.setDepth(next);
        checkList.setDepth(next);
    }

    private static class PersonCellFactory extends MFXListCell<Person> {

        public PersonCellFactory(Person data) {
            super(data);

            MFXFontIcon userIcon = new MFXFontIcon("fas-user", 18);
            userIcon.getStyleClass().add("user-icon");
            setGraphic(userIcon);
        }
    }
}
