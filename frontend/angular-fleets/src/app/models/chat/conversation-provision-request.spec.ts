import { ConversationProvisionRequest } from './conversation-provision-request';

describe('GetConversationRequest', () => {
  it('should create an instance', () => {
    expect(new ConversationProvisionRequest('Test Listing', 42)).toBeTruthy();
  });
});
