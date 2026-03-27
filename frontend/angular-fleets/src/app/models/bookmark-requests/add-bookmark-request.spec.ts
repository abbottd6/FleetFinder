import { AddBookmarkRequest } from './add-bookmark-request';

describe('AddBookmark', () => {
  it('should create an instance', () => {
    expect(new AddBookmarkRequest(1)).toBeTruthy();
  });
});
