export interface Page<T> {
  content: T[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  }
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
    asc: boolean;
    desc: boolean;
  }
}

export function newEmptyPage<T>(): Page<T> {
  return {
    content: [],
    page: {
      size: 0,
      number: 0,
      totalElements: 0,
      totalPages: 0,
    },
    sort: {
      empty: true,
      sorted: false,
      unsorted: true,
      asc: false,
      desc: true
    }
  }
}
