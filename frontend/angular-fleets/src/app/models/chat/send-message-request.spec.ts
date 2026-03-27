import { SendMessageRequest } from './send-message-request';

describe('SendMessageRequest', () => {
  it('should create an instance', () => {
    expect(new SendMessageRequest({} as any, 1, 'TEXT', 'hello')).toBeTruthy();
  });
});
