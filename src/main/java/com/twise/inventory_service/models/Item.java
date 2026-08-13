package com.twise.inventory_service.models;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


public class Item {
        @Id
        private Long id;

        private String name;
        private Double price;
        private LocalDate dateBought;
        private LocalDate expirationDate;
        private List<Tag> tags;
        private Integer quantity;
        private String quantityUnit;

        public Item(Long id,
                    String name,
                    Double price,
                    LocalDate dateBought,
                    LocalDate expirationDate,
                    Integer quantity,
                    String quantityUnit) {
                this.id = id;
                this.name = name;
                this.price = price;
                this.dateBought = dateBought;
                this.expirationDate = expirationDate;
                this.quantity = quantity;
                this.quantityUnit = quantityUnit;
        }


        public void setTags(List<Tag> tags) {
                this.tags = tags;
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public Double getPrice() {
                return price;
        }

        public void setPrice(Double price) {
                this.price = price;
        }

        public LocalDate getDateBought() {
                return dateBought;
        }

        public void setDateBought(LocalDate dateBought) {
                this.dateBought = dateBought;
        }

        public LocalDate getExpirationDate() {
                return expirationDate;
        }

        public void setExpirationDate(LocalDate expirationDate) {
                this.expirationDate = expirationDate;
        }

        public List<Tag> getTags() {
                return tags;
        }

        public Integer getQuantity(){
                return quantity;
        }

        public void setQuantity(Integer quantity){
                this.quantity = quantity;
        }

        public String getQuantityUnit(){
                return quantityUnit;
        }

        public void setQuantityUnit(String quantityUnit){
                this.quantityUnit = quantityUnit;
        }

        public static Item of(String name,
                       Double price,
                       LocalDate dateBought,
                       LocalDate expirationDate,
                       Integer quantity,
                       String quantityUnit){
                return new Item(null, name, price, dateBought, expirationDate, quantity, quantityUnit);
        }
}
