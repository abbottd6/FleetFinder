export class BoundedHistoryStack<T> {
  private stack: T[] = [];

  constructor(private readonly stackLimit: number) {}

  push(snapshot: T): void {
    this.stack.push(snapshot);
    if (this.stack.length > this.stackLimit) {
      this.stack.shift();
    }
  }

  pop(): T | undefined {
    return this.stack.pop();
  }

  peek(): T | undefined {
    return this.stack.at(-1);
  }

  get canUndo(): boolean {
    return this.stack.length > 0;
  }

  clearStack(): void {
    this.stack = [];
  }
}
