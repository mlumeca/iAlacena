package com.luisa.iAlacena.shoppingcart.model;

import com.luisa.iAlacena.user.model.User;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "shopping_cart")
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_shoppingcart_user")
    )
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(
            name = "shopping_cart_item",
            joinColumns = @JoinColumn(name = "shopping_cart_id")
    )
    @MapKeyJoinColumn(name = "ingredient_id")
    @Column(name = "quantity")
    @Builder.Default
    private Map<Ingredient, Integer> items = new HashMap<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void addIngredient(Ingredient ingredient) {
        items.merge(ingredient, 1, Integer::sum);
    }

    public void removeIngredient(Ingredient ingredient) {
        Integer currentQuantity = items.get(ingredient);
        if (currentQuantity != null) {
            int newQuantity = currentQuantity - 1;
            if (newQuantity <= 0) {
                items.remove(ingredient);
            } else {
                items.put(ingredient, newQuantity);
            }
        }
    }
}