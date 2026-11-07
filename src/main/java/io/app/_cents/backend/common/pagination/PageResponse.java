package io.app._cents.backend.common.pagination;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
    List<T> data,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious) {

  private static final int MAX_SIZE = 100;

  // For an in-memory list: pass the full list, get one page back
  public static <T> PageResponse<T> of(List<T> data, int pageNumber, int pageSize) {
    int safeSize = Math.min(Math.max(pageSize, 1), MAX_SIZE);
    int safePage = Math.max(pageNumber, 0);
    long total = data.size();
    int totalPages = (int) Math.ceil((double) total / safeSize);
    int from = (int) Math.min((long) safePage * safeSize, total);
    int to = (int) Math.min((long) from + safeSize, total);

    return new PageResponse<>(
        List.copyOf(data.subList(from, to)),
        safePage,
        safeSize,
        total,
        totalPages,
        safePage + 1 < totalPages,
        safePage > 0);
  }

  // For database queries: convert a Spring Data Page directly
  public static <T> PageResponse<T> from(Page<T> p) {
    return new PageResponse<>(
        p.getContent(),
        p.getNumber(),
        p.getSize(),
        p.getTotalElements(),
        p.getTotalPages(),
        p.hasNext(),
        p.hasPrevious());
  }

  public static <T> PageResponse<T> toPaginated(List<T> rows, int page, int size, long total) {
    int totalPages = (int) Math.ceil((double) total / size);
    return new PageResponse<>(
        rows, page, size, total, totalPages, page + 1 <= totalPages, (page > 1 && totalPages > 0));
  }
}
