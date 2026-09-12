export function RoutesPagination({
                                   page,
                                   totalPages,
                                   isLoading = false,
                                   onPageChange,
                                 }) {
  if (totalPages <= 1) {
    return null;
  }

  const isFirstPage = page === 0;
  const isLastPage = page >= totalPages - 1;

  return (
    <nav aria-label="Paginazione itinerari">
      <button
        type="button"
        disabled={isLoading || isFirstPage}
        onClick={() => onPageChange(page - 1)}
      >
        Pagina precedente
      </button>

      <span aria-live="polite">
        Pagina {page + 1} di {totalPages}
      </span>

      <button
        type="button"
        disabled={isLoading || isLastPage}
        onClick={() => onPageChange(page + 1)}
      >
        Pagina successiva
      </button>
    </nav>
  );
}
