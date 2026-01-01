import { ConversationViewModel } from './conversation-view-model';

describe('ConversationViewModel', () => {
  it('should create an instance', () => {
    expect(new ConversationViewModel(1, 'DIRECT', 'Title', 25,
      'otherUser', new Date('2025-12-31T17:42:21.906Z'), new Date('2025-12-31T18:42:21.906Z'),
      10, 30, 'currUser', 'dmKey')).toBeTruthy();
  });
});
