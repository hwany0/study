package lab.app.product;

import jakarta.persistence.*;

// NOTE: category_id 에 인덱스를 일부러 걸지 않았다. (Week 1 실험용)
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private long price;
    private int stock;

    @Column(name = "category_id")
    private long categoryId;

    protected Product() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public long getPrice() { return price; }
    public int getStock() { return stock; }
    public long getCategoryId() { return categoryId; }
}
