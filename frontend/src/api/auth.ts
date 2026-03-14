import client from './client';
import type { AuthRequest, AuthResponse } from '../types/auth';

export const signin = (data: AuthRequest) =>
  client.post<AuthResponse>('/auth/signin', data).then((r) => r.data);

export const signup = (data: AuthRequest) =>
  client.post('/auth/signup', data).then((r) => r.data);
