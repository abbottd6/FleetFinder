import { MessageViewModel } from './message-view-model';

describe('MessageViewModel', () => {
  it('should create an instance', () => {
    expect(new MessageViewModel(1, 1, 1, 'user', 'TEXT', 'hello', new Date(), new Date(), null, null, 'abc')).toBeTruthy();
  });
});
