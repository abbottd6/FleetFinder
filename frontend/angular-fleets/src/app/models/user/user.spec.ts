import { User } from './user';

describe('User', () => {
  it('should create an instance', () => {
    expect(new User(44, 'Angular Test User',
      'test@gmail.com', 'USA', 'None', 'like to fly space ships', new Date('2024-01-09'),
      [])).toBeTruthy();
  });
});
