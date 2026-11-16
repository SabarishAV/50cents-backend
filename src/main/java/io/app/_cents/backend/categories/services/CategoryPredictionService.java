package io.app._cents.backend.categories.services;

import io.app._cents.backend.categories.CategoriesRepository;
import io.app._cents.backend.categories.dtos.predict.PredictCategoryRequest;
import io.app._cents.backend.categories.models.Category;
import io.app._cents.backend.categories.services.llm.GroqCategoryPredictor;
import io.app._cents.backend.payments.models.enums.TransactionType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryPredictionService {

  private static final int NAME_WEIGHT = 3;
  private static final int NOTE_WEIGHT = 1;

  private static final String FALLBACK_CODE = "OTHER";
  private static final String CREDIT_FALLBACK_CODE = "OTHER_INCOME";
  private static final Set<String> INCOME_ONLY =
      Set.of("SALARY", "REFUND", "INTEREST", "CASHBACK", "OTHER_INCOME");
  private static final Set<String> BOTH_WAYS = Set.of("TRANSFERS", "INVESTMENTS", "OTHER");

  // Order = tie-break priority (earlier wins on equal score)
  private static final Map<String, List<String>> KEYWORDS = new LinkedHashMap<>();

  static {
    put(
        "FOOD_DINING",
        "swiggy",
        "zomato",
        "restaurant",
        "cafe",
        "coffee",
        "tea",
        "bakery",
        "biryani",
        "biriyani",
        "hotel",
        "dhaba",
        "mess",
        "canteen",
        "pizza",
        "burger",
        "dominos",
        "kfc",
        "mcdonald",
        "starbucks",
        "juice",
        "lunch",
        "dinner",
        "breakfast",
        "snack",
        "food",
        "ice cream",
        "bar");
    put(
        "GROCERIES",
        "bigbasket",
        "blinkit",
        "zepto",
        "instamart",
        "supermarket",
        "grocery",
        "groceries",
        "dmart",
        "reliance fresh",
        "more",
        "vegetable",
        "fruits",
        "milk",
        "provision",
        "stores",
        "mart");
    put(
        "FUEL",
        "petrol",
        "diesel",
        "fuel",
        "hpcl",
        "bpcl",
        "iocl",
        "indian oil",
        "shell",
        "gas station",
        "pump",
        "cng");
    put(
        "TRANSPORT",
        "uber",
        "ola",
        "rapido",
        "metro",
        "bus",
        "auto",
        "taxi",
        "cab",
        "parking",
        "toll",
        "fastag",
        "ksrtc",
        "namma yatri",
        "train",
        "ticket");
    put(
        "TRAVEL",
        "irctc",
        "redbus",
        "makemytrip",
        "goibibo",
        "cleartrip",
        "flight",
        "indigo",
        "air india",
        "airbnb",
        "oyo",
        "booking",
        "resort",
        "trip",
        "holiday",
        "visa");
    put(
        "SUBSCRIPTIONS",
        "netflix",
        "spotify",
        "prime",
        "hotstar",
        "youtube premium",
        "subscription",
        "membership",
        "icloud",
        "google one",
        "chatgpt",
        "claude",
        "sonyliv",
        "zee5");
    put(
        "ENTERTAINMENT",
        "bookmyshow",
        "movie",
        "cinema",
        "pvr",
        "inox",
        "theatre",
        "game",
        "gaming",
        "concert",
        "steam",
        "playstation",
        "event");
    put(
        "SHOPPING",
        "amazon",
        "flipkart",
        "myntra",
        "ajio",
        "meesho",
        "nykaa",
        "mall",
        "clothes",
        "shoes",
        "fashion",
        "textiles",
        "electronics",
        "croma",
        "decathlon",
        "shopping");
    put(
        "MOBILE_INTERNET",
        "jio",
        "airtel",
        "vodafone",
        "bsnl",
        "recharge",
        "broadband",
        "wifi",
        "internet",
        "data pack",
        "prepaid",
        "postpaid",
        "act fibernet");
    put(
        "BILLS_UTILITIES",
        "electricity",
        "kseb",
        "water bill",
        "kwa",
        "gas bill",
        "dth",
        "tata play",
        "bill",
        "bescom",
        "utility",
        "cylinder",
        "lpg");
    put("RENT", "rent", "landlord", "lease", "pg", "hostel", "deposit");
    put(
        "HOME_MAINTENANCE",
        "plumber",
        "electrician",
        "carpenter",
        "repair",
        "maintenance",
        "furniture",
        "ikea",
        "urban company",
        "paint",
        "hardware",
        "cleaning",
        "society");
    put(
        "HEALTH_MEDICAL",
        "pharmacy",
        "medical",
        "hospital",
        "clinic",
        "doctor",
        "apollo",
        "medplus",
        "pharmeasy",
        "1mg",
        "lab",
        "diagnostic",
        "dental",
        "medicine",
        "scan");
    put(
        "PERSONAL_CARE",
        "salon",
        "spa",
        "barber",
        "haircut",
        "parlour",
        "grooming",
        "gym",
        "cult fit",
        "fitness",
        "beauty");
    put(
        "EDUCATION",
        "school",
        "college",
        "university",
        "tuition",
        "course",
        "udemy",
        "coursera",
        "exam",
        "fees",
        "books",
        "library",
        "coaching",
        "byju");
    put("INSURANCE", "insurance", "lic", "premium", "policy", "acko", "digit", "star health");
    put(
        "INVESTMENTS",
        "zerodha",
        "groww",
        "upstox",
        "mutual fund",
        "sip",
        "stocks",
        "shares",
        "gold",
        "nps",
        "ppf",
        "fd",
        "crypto",
        "coin");
    put("LOANS_EMI", "emi", "loan", "bajaj finance", "credit card", "installment", "repayment");
    put(
        "TAXES_FEES",
        "tax",
        "gst",
        "challan",
        "fine",
        "penalty",
        "income tax",
        "registration",
        "stamp duty",
        "charges");
    put(
        "GIFTS_DONATIONS",
        "gift",
        "donation",
        "temple",
        "church",
        "mosque",
        "charity",
        "birthday",
        "wedding",
        "ngo");
    put("FAMILY_KIDS", "kids", "toys", "daycare", "baby", "diaper", "firstcry", "school bus");
    put("PETS", "pet", "vet", "dog", "cat", "pet food", "heads up for tails");
    put("CASH_WITHDRAWAL", "atm", "cash withdrawal", "withdraw", "withdrawal");
    put("TRANSFERS", "transfer", "sent to", "imps", "neft", "rtgs", "self");
    put("SALARY", "salary", "payroll", "stipend", "wages", "bonus", "incentive");
    put("REFUND", "refund", "reversal", "reversed", "chargeback", "returned");
    put("INTEREST", "interest", "dividend");
    put("CASHBACK", "cashback", "reward", "rewards", "cash back");
  }

  private static void put(String code, String... keywords) {
    KEYWORDS.put(code, List.of(keywords));
  }

  private final CategoriesRepository categoriesRepository;
  private final GroqCategoryPredictor groqCategoryPredictor;

  @Transactional(readOnly = true)
  public String predictCode(PredictCategoryRequest req) {
    List<Category> categories = categoriesRepository.findAll();
    Set<String> validCodes = categories.stream().map(Category::getCode).collect(Collectors.toSet());

    boolean isCredit = req != null && req.getTransactionType() == TransactionType.CREDIT;
    String fallback = resolveFallback(isCredit, validCodes);
    Set<String> allowedCodes = resolveAllowedCodes(isCredit, validCodes);

    if (groqCategoryPredictor != null && groqCategoryPredictor.isEnabled()) {
      Optional<String> llmPrediction =
          groqCategoryPredictor.predict(
              req == null ? null : req.getRecipient(),
              req == null ? null : req.getNote(),
              req == null ? null : req.getTransactionType(),
              categories,
              allowedCodes);
      if (llmPrediction.isPresent()) {
        return llmPrediction.get();
      }
    }

    String keywordPrediction = predictByKeywords(req, isCredit, validCodes);
    if (keywordPrediction != null) {
      return keywordPrediction;
    }
    if (fallback != null) {
      return fallback;
    }
    return validCodes.contains(FALLBACK_CODE)
        ? FALLBACK_CODE
        : validCodes.stream().findFirst().orElse(FALLBACK_CODE);
  }

  private String predictByKeywords(
      PredictCategoryRequest req, boolean isCredit, Set<String> validCodes) {
    String name = normalize(req == null ? null : req.getRecipient());
    String note = normalize(req == null ? null : req.getNote());

    String bestCode = null;
    int bestScore = 0;

    for (Map.Entry<String, List<String>> entry : KEYWORDS.entrySet()) {
      String code = entry.getKey();
      if (!validCodes.contains(code)) continue;
      if (!isAllowedForType(code, isCredit)) continue;

      int score = 0;
      for (String kw : entry.getValue()) {
        if (kw == null || kw.isBlank()) continue;
        int words = kw.trim().split("\\s+").length;
        if (matches(name, kw)) score += NAME_WEIGHT * words;
        if (matches(note, kw)) score += NOTE_WEIGHT * words;
      }
      if (score > bestScore) {
        bestScore = score;
        bestCode = code;
      }
    }

    return bestCode;
  }

  private static Set<String> resolveAllowedCodes(boolean isCredit, Set<String> validCodes) {
    return validCodes.stream()
        .filter(code -> isAllowedForType(code, isCredit))
        .collect(Collectors.toSet());
  }

  private static boolean isAllowedForType(String code, boolean isCredit) {
    return BOTH_WAYS.contains(code) || (isCredit == INCOME_ONLY.contains(code));
  }

  private static String resolveFallback(boolean isCredit, Set<String> validCodes) {
    if (isCredit && validCodes.contains(CREDIT_FALLBACK_CODE)) {
      return CREDIT_FALLBACK_CODE;
    }
    if (validCodes.contains(FALLBACK_CODE)) {
      return FALLBACK_CODE;
    }
    return null;
  }

  /** lowercase, strip punctuation, pad with spaces so word-boundary checks are easy */
  private static String normalize(String s) {
    String cleaned = s == null ? "" : s.toLowerCase().replaceAll("[^a-z0-9]+", " ").trim();
    return " " + cleaned + " ";
  }

  /** Short keywords must match a whole word; longer ones match word prefixes (handles plurals). */
  private static boolean matches(String text, String keyword) {
    return keyword.length() <= 4
        ? text.contains(" " + keyword + " ")
        : text.contains(" " + keyword);
  }
}
