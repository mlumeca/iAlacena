package com.luisa.iAlacena.shoppingcart.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "shopping_cart_item")
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "shopping_cart_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_shoppingcartitem_cart")
    )
    private ShoppingCart shoppingCart;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "ingredient_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_shoppingcartitem_ingredient")
    )
    private Ingredient ingredient;

    @Column(nullable = false)
    private int quantity;

    public Long getShoppingCartId() {
        return shoppingCart != null ? shoppingCart.getId() : null;
    }

    public Long getIngredientId() {
        return ingredient != null ? ingredient.getId() : null;
    }
}