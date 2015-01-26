package ch.rasc.s4ws.grid;

import java.math.BigDecimal;

public class Product {
	private int id;

	private String name;

	private String orderNumber;

	private int weight;

	private int size;

	private boolean inStock;

	private BigDecimal price;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrderNumber() {
		return this.orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getWeight() {
		return this.weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isInStock() {
		return this.inStock;
	}

	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
