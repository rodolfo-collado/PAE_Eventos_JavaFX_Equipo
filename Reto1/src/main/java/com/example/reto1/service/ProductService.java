package com.example.reto1.service;

import com.example.reto1.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private final List<Product> products;

    public ProductService() {
        this.products = new ArrayList<>();
    }

    public boolean addProduct(Product product) {
        if (product == null || product.getId() == null || product.getId().trim().isEmpty()) {
            return false;
        }
        if (findByCode(product.getId()) != null) {
            return false;
        }
        return products.add(product);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public Product findByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        String cleanCode = code.trim();
        for (Product product : products) {
            if (product.getId() != null && product.getId().equalsIgnoreCase(cleanCode)) {
                return product;
            }
        }
        return null;
    }

    public List<Product> findByNameStartingWith(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return getAllProducts();
        }
        String cleanPrefix = prefix.trim().toLowerCase();
        List<Product> matches = new ArrayList<>();
        for (Product product : products) {
            if (product.getName() != null && product.getName().toLowerCase().startsWith(cleanPrefix)) {
                matches.add(product);
            }
        }
        return matches;
    }


    /**
     * Filtra productos con precio mayor al especificado.
     * @param minPrice Precio mínimo.
     * @return Lista de productos que cumplen la condición.
     */
    public List<Product> filterByPriceGreaterThan(double minPrice) {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (product.getPrice() > minPrice) {
                result.add(product);
            }
        }
        return result;
    }

    /**
     * Filtra productos con precio menor al especificado.
     * @param maxPrice Precio máximo.
     * @return Lista de productos que cumplen la condición.
     */
    public List<Product> filterByPriceLessThan(double maxPrice) {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (product.getPrice() < maxPrice) {
                result.add(product);
            }
        }
        return result;
    }

    /**
     * Filtra productos con precio igual al especificado.
     * @param price Precio a comparar.
     * @return Lista de productos que cumplen la condición.
     */
    public List<Product> filterByPriceEquals(double price) {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (Math.abs(product.getPrice() - price) < 0.0001) {
                result.add(product);
            }
        }
        return result;
    }

    /**
     * Filtra productos por precio según una condición en texto (Mayor que, Menor que, Igual a o símbolos >, <, =).
     * @param condition Condición ("Mayor que (>)", "Menor que (<)", "Igual a (=)", ">", "<", "=").
     * @param price Valor del precio.
     * @return Lista de productos filtrados.
     */
    public List<Product> filterByPrice(String condition, double price) {
        if (condition == null || condition.trim().isEmpty()) {
            return getAllProducts();
        }
        String cond = condition.toLowerCase();
        if (cond.contains(">") || cond.contains("mayor")) {
            return filterByPriceGreaterThan(price);
        } else if (cond.contains("<") || cond.contains("menor")) {
            return filterByPriceLessThan(price);
        } else if (cond.contains("=") || cond.contains("igual")) {
            return filterByPriceEquals(price);
        }
        return getAllProducts();
    }

    /**
     * Filtra productos con stock mayor al especificado.
     * @param minStock Cantidad mínima.
     * @return Lista de productos que cumplen la condición.
     */
    public List<Product> filterByStockGreaterThan(double minStock) {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (product.getStock() > minStock) {
                result.add(product);
            }
        }
        return result;
    }

    /**
     * Filtra productos con stock menor al especificado.
     * @param maxStock Cantidad máxima.
     * @return Lista de productos que cumplen la condición.
     */
    public List<Product> filterByStockLessThan(double maxStock) {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (product.getStock() < maxStock) {
                result.add(product);
            }
        }
        return result;
    }

    /**
     * Filtra productos con stock igual al especificado.
     * @param stock Cantidad exacta a comparar.
     * @return Lista de productos que cumplen la condición.
     */
    public List<Product> filterByStockEquals(double stock) {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (Math.abs(product.getStock() - stock) < 0.0001) {
                result.add(product);
            }
        }
        return result;
    }

    /**
     * Filtra productos por stock según una condición en texto (Mayor que, Menor que, Igual a o símbolos >, <, =).
     * @param condition Condición ("Mayor que (>)", "Menor que (<)", "Igual a (=)", ">", "<", "=").
     * @param stock Valor de la cantidad/stock.
     * @return Lista de productos filtrados.
     */
    public List<Product> filterByStock(String condition, double stock) {
        if (condition == null || condition.trim().isEmpty()) {
            return getAllProducts();
        }
        String cond = condition.toLowerCase();
        if (cond.contains(">") || cond.contains("mayor")) {
            return filterByStockGreaterThan(stock);
        } else if (cond.contains("<") || cond.contains("menor")) {
            return filterByStockLessThan(stock);
        } else if (cond.contains("=") || cond.contains("igual")) {
            return filterByStockEquals(stock);
        }
        return getAllProducts();
    }

    /**
     * Aplica simultáneamente todos los filtros disponibles en la vista (Búsqueda por texto, precio y stock).
     *
     * @param searchText      Texto ingresado en el buscador (puede ser null o vacío si no se busca por texto).
     * @param searchByName    true si la búsqueda de texto es por nombre (starts with), false si es por código exacto.
     * @param priceCondition  Condición de precio seleccionada en el ComboBox (puede ser null o vacío).
     * @param targetPrice     Valor de precio ingresado (puede ser null si no se filtra por precio).
     * @param stockCondition  Condición de stock seleccionada en el ComboBox (puede ser null o vacío).
     * @param targetStock     Valor de stock ingresado (puede ser null si no se filtra por stock).
     * @return Lista de productos que cumplen todos los criterios activos.
     */
    public List<Product> filter(String searchText, boolean searchByName,
                                String priceCondition, Double targetPrice,
                                String stockCondition, Double targetStock) {
        List<Product> result = new ArrayList<>();

        for (Product product : products) {
            // 1. Filtro de Texto (Nombre o Código)
            if (searchText != null && !searchText.trim().isEmpty()) {
                String cleanSearch = searchText.trim();
                if (searchByName) {
                    if (product.getName() == null || !product.getName().toLowerCase().startsWith(cleanSearch.toLowerCase())) {
                        continue;
                    }
                } else {
                    if (product.getId() == null || !product.getId().equalsIgnoreCase(cleanSearch)) {
                        continue;
                    }
                }
            }

            // 2. Filtro de Precio
            if (priceCondition != null && targetPrice != null) {
                String pCond = priceCondition.toLowerCase();
                if (pCond.contains(">") || pCond.contains("mayor")) {
                    if (!(product.getPrice() > targetPrice)) continue;
                } else if (pCond.contains("<") || pCond.contains("menor")) {
                    if (!(product.getPrice() < targetPrice)) continue;
                } else if (pCond.contains("=") || pCond.contains("igual")) {
                    if (Math.abs(product.getPrice() - targetPrice) >= 0.0001) continue;
                }
            }

            // 3. Filtro de Stock
            if (stockCondition != null && targetStock != null) {
                String sCond = stockCondition.toLowerCase();
                if (sCond.contains(">") || sCond.contains("mayor")) {
                    if (!(product.getStock() > targetStock)) continue;
                } else if (sCond.contains("<") || sCond.contains("menor")) {
                    if (!(product.getStock() < targetStock)) continue;
                } else if (sCond.contains("=") || sCond.contains("igual")) {
                    if (Math.abs(product.getStock() - targetStock) >= 0.0001) continue;
                }
            }

            // Cumple todos los filtros activos
            result.add(product);
        }

        return result;
    }

    public boolean updateProduct(Product updatedProduct) {
        if (updatedProduct == null || updatedProduct.getId() == null) {
            return false;
        }
        for (int i = 0; i < products.size(); i++) {
            Product current = products.get(i);
            if (current.getId() != null && current.getId().equalsIgnoreCase(updatedProduct.getId().trim())) {
                products.set(i, updatedProduct);
                return true;
            }
        }
        return false;
    }

    public boolean deleteByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        String cleanCode = code.trim();
        return products.removeIf(p -> p.getId() != null && p.getId().equalsIgnoreCase(cleanCode));
    }

    public boolean deleteProduct(Product product) {
        if (product == null) {
            return false;
        }
        return products.remove(product);
    }
}

