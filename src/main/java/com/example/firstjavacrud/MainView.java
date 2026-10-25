package com.example.firstjavacrud;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {

    private final CustomerRepository repo;
    private final Grid<Customer> grid = new Grid<>(Customer.class, false);
    private final TextField filter = new TextField();
    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final Button addNew = new Button("New customer");
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete");
    private final HorizontalLayout editor =
            new HorizontalLayout(firstName, lastName, save, cancel, delete);
    private final Binder<Customer> binder = new Binder<>(Customer.class);

    private Customer customer;

    public MainView(CustomerRepository repo) {
        this.repo = repo;

        grid.addColumn(Customer::getId).setHeader("ID");
        grid.addColumn(Customer::getFirstName).setHeader("First name");
        grid.addColumn(Customer::getLastName).setHeader("Last name");

        filter.setPlaceholder("Filter by last name");
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listCustomers(e.getValue()));

        binder.forField(firstName).bind(Customer::getFirstName, Customer::setFirstName);
        binder.forField(lastName).bind(Customer::getLastName, Customer::setLastName);

        grid.asSingleSelect().addValueChangeListener(e -> editCustomer(e.getValue()));
        addNew.addClickListener(e -> editCustomer(new Customer("", "")));

        save.addClickListener(e -> {
            repo.save(customer);
            editCustomer(null);
            listCustomers(filter.getValue());
        });
        delete.addClickListener(e -> {
            repo.delete(customer);
            editCustomer(null);
            listCustomers(filter.getValue());
        });
        cancel.addClickListener(e -> {
            editCustomer(null);
            listCustomers(filter.getValue());
        });

        editor.setVisible(false);
        add(new H2("Customers"), new HorizontalLayout(filter, addNew), grid, editor);
        listCustomers(null);
    }

    private void listCustomers(String name) {
        if (name == null || name.isEmpty()) {
            grid.setItems(repo.findAll());
        } else {
            grid.setItems(repo.findByLastNameStartsWithIgnoreCase(name));
        }
    }

    private void editCustomer(Customer c) {
        if (c == null) {
            editor.setVisible(false);
            return;
        }
        customer = c;
        binder.setBean(customer);
        editor.setVisible(true);
        firstName.focus();
    }
}
