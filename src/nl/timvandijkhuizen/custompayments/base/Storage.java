package nl.timvandijkhuizen.custompayments.base;

import java.util.List;

import nl.timvandijkhuizen.custompayments.elements.Category;
import nl.timvandijkhuizen.custompayments.elements.Command;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.spigotutils.services.Service;

public abstract class Storage implements Service {

    @Override
    public String getHandle() {
        return "storage";
    }

    /**
     * Returns all categories.
     * 
     * @return
     * @throws Exception
     */
    public abstract List<Category> getCategories() throws Exception;

    /**
     * Creates a category.
     * 
     * @param category
     * @throws Exception
     */
    public abstract void createCategory(Category category) throws Exception;

    /**
     * Updates a category.
     * 
     * @param category
     * @throws Exception
     */
    public abstract void updateCategory(Category category) throws Exception;

    /**
     * Deletes a category.
     * 
     * @param category
     * @throws Exception
     */
    public abstract void deleteCategory(Category category) throws Exception;

    /**
     * Returns all products.
     * 
     * @return
     * @throws Exception
     */
    public abstract List<Product> getProducts() throws Exception;

    /**
     * Creates a product.
     * 
     * @param product
     * @throws Exception
     */
    public abstract void createProduct(Product product) throws Exception;

    /**
     * Updates a product.
     * 
     * @param product
     * @throws Exception
     */
    public abstract void updateProduct(Product product) throws Exception;

    /**
     * Deletes a product.
     * 
     * @param product
     * @throws Exception
     */
    public abstract void deleteProduct(Product product) throws Exception;

    /**
     * Returns all commands belonging to the specified product.
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public abstract List<Command> getCommandsByProductId(int productId) throws Exception;

    /**
     * Creates a command.
     * 
     * @param command
     * @throws Exception
     */
    public abstract void createCommand(Command command) throws Exception;

    /**
     * Deletes a command.
     * 
     * @param command
     * @throws Exception
     */
    public abstract void deleteCommand(Command command) throws Exception;

    /**
     * Creates a field
     * 
     * @param field
     * @throws Exception
     */
    public abstract void createField(Field<?> field) throws Exception;

    /**
     * Updates a field
     * 
     * @param field
     * @throws Exception
     */
    public abstract void updateField(Field<?> field) throws Exception;

    /**
     * Deletes a field.
     * 
     * @param field
     * @throws Exception
     */
    public abstract void deleteField(Field<?> field) throws Exception;

}
