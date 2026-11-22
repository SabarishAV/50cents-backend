package io.app._cents.backend.seeders.category;

import io.app._cents.backend.categories.CategoriesRepository;
import io.app._cents.backend.categories.models.Category;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Conditional(CategorySeedingCondition.class)
@Order(2)
public class CategorySeeder implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(CategorySeeder.class);

  private final CategoriesRepository categoriesRepository;

  public CategorySeeder(CategoriesRepository categoriesRepository) {
    this.categoriesRepository = categoriesRepository;
  }

  private static Category category(String name, String code, String image) {
    Category c = new Category();
    c.setName(name);
    c.setCode(code);
    c.setImage(image);
    return c;
  }

  public static final List<Category> CATEGORIES =
      List.of(
          category("Food & Dining", "FOOD_DINING", null),
          category("Groceries", "GROCERIES", null),
          category("Transport", "TRANSPORT", null),
          category("Fuel", "FUEL", null),
          category("Shopping", "SHOPPING", null),
          category("Bills & Utilities", "BILLS_UTILITIES", null),
          category("Mobile & Internet", "MOBILE_INTERNET", null),
          category("Rent", "RENT", null),
          category("Home & Maintenance", "HOME_MAINTENANCE", null),
          category("Entertainment", "ENTERTAINMENT", null),
          category("Subscriptions", "SUBSCRIPTIONS", null),
          category("Travel", "TRAVEL", null),
          category("Health & Medical", "HEALTH_MEDICAL", null),
          category("Personal Care", "PERSONAL_CARE", null),
          category("Education", "EDUCATION", null),
          category("Insurance", "INSURANCE", null),
          category("Investments", "INVESTMENTS", null),
          category("Loans & EMI", "LOANS_EMI", null),
          category("Taxes & Fees", "TAXES_FEES", null),
          category("Gifts & Donations", "GIFTS_DONATIONS", null),
          category("Family & Kids", "FAMILY_KIDS", null),
          category("Pets", "PETS", null),
          category("Cash Withdrawal", "CASH_WITHDRAWAL", null),
          category("Transfers", "TRANSFERS", null),
          category("Other", "OTHER", null),
          category("Salary", "SALARY", null),
          category("Refund", "REFUND", null),
          category("Interest", "INTEREST", null),
          category("Cashback", "CASHBACK", null),
          category("Other Income", "OTHER_INCOME", null));

  @Override
  public void run(String... args) {
    Set<String> existing =
        categoriesRepository.findAll().stream().map(Category::getCode).collect(Collectors.toSet());

    List<Category> toInsert =
        CATEGORIES.stream().filter(c -> !existing.contains(c.getCode())).toList();

    categoriesRepository.saveAll(toInsert);
    log.info("Seeded {} new categories ({} already existed)", toInsert.size(), existing.size());
  }
}
