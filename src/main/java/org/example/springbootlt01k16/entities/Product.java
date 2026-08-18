package org.example.springbootlt01k16.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    //id, name, price, quantity, description, brand_id, category_id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private String Name;

    @Column(columnDefinition = "DOUBLE CHECK(price > 0)")
    private Double Price;

    @Column(columnDefinition = "INT CHECK(quantity > 0)")
    private Integer Quantity;

    @Column(columnDefinition = "TEXT")
    private String Description;

    //nhiều product thuộc về 1 brand
    @ManyToOne(fetch = FetchType.LAZY)
    //Cột FK
    @JoinColumn(name = "brand_id")
    private Brand brand;

    //Nhiều product thuộc về 1 category
    @ManyToOne(fetch = FetchType.LAZY)
    //Cột FK
    @JoinColumn(name = "category_id")
    private Category category;
}
