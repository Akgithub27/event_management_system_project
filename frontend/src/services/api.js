import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle response errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  signup: (data) => api.post('/auth/signup', data),
  login: (data) => api.post('/auth/login', data),
};

export const eventService = {
  getAllEvents: () => api.get('/events'),
  getUpcomingEvents: () => api.get('/events/upcoming'),
  getEventById: (id) => api.get(`/events/${id}`),
  searchEvents: (query) => api.get(`/events/search?q=${query}`),
  getEventsByCategory: (category) => api.get(`/events/category/${category}`),
  createEvent: (data) => api.post('/events', data),
  updateEvent: (id, data) => api.put(`/events/${id}`, data),
  deleteEvent: (id) => api.delete(`/events/${id}`),
};

export const registrationService = {
  registerForEvent: (eventId) => api.post(`/registrations/events/${eventId}`),
  cancelRegistration: (eventId) => api.delete(`/registrations/events/${eventId}`),
  getUserRegistrations: () => api.get('/registrations/my-registrations'),
  getEventRegistrations: (eventId) => api.get(`/registrations/events/${eventId}`),
  markAsAttended: (eventId, userId) => api.post(`/registrations/attend/${eventId}/${userId}`),
};

export default api;
