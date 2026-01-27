import React, { useEffect, useState, useContext } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { eventService } from '../services/api';
import { AuthContext } from '../context/AuthContext';
import EventCard from '../components/EventCard';
import '../styles/Dashboard.css';

export const Dashboard = () => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [filterCategory, setFilterCategory] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useContext(AuthContext);

  useEffect(() => {
    fetchEvents();
  }, []);

  useEffect(() => {
    const state = location.state;
    if (state?.message) {
      setError('');
      // Refresh events after event creation
      fetchEvents();
    }
  }, [location]);

  const fetchEvents = async () => {
    try {
      setLoading(true);
      const response = await eventService.getAllEvents();
      setEvents(response.data.data || []);
    } catch (err) {
      setError('Failed to load events');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchTerm.trim()) {
      fetchEvents();
      return;
    }

    try {
      const response = await eventService.searchEvents(searchTerm);
      setEvents(response.data.data || []);
    } catch (err) {
      setError('Search failed');
    }
  };

  const handleFilterByCategory = async (category) => {
    setFilterCategory(category);
    if (!category) {
      fetchEvents();
      return;
    }

    try {
      const response = await eventService.getEventsByCategory(category);
      setEvents(response.data.data || []);
    } catch (err) {
      setError('Filter failed');
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleCreateEvent = () => {
    if (user?.role === 'ADMIN') {
      navigate('/create-event');
    } else {
      setError('Only admins can create events');
    }
  };

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-content">
          <h1>Event Management System</h1>
          <div className="user-info">
            <span>Welcome, {user?.firstName}!</span>
            <button onClick={handleLogout} className="btn-logout">Logout</button>
          </div>
        </div>
      </header>

      <div className="dashboard-content">
        <div className="search-section">
          <form onSubmit={handleSearch} className="search-form">
            <input
              type="text"
              placeholder="Search events..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
            <button type="submit" className="btn-search">Search</button>
          </form>

          <div className="filter-buttons">
            <button
              className={`filter-btn ${!filterCategory ? 'active' : ''}`}
              onClick={() => handleFilterByCategory('')}
            >
              All Events
            </button>
            <button
              className={`filter-btn ${filterCategory === 'Tech' ? 'active' : ''}`}
              onClick={() => handleFilterByCategory('Tech')}
            >
              Tech
            </button>
            <button
              className={`filter-btn ${filterCategory === 'Business' ? 'active' : ''}`}
              onClick={() => handleFilterByCategory('Business')}
            >
              Business
            </button>
            <button
              className={`filter-btn ${filterCategory === 'Workshop' ? 'active' : ''}`}
              onClick={() => handleFilterByCategory('Workshop')}
            >
              Workshop
            </button>
            <button
              className={`filter-btn ${filterCategory === 'Networking' ? 'active' : ''}`}
              onClick={() => handleFilterByCategory('Networking')}
            >
              Networking
            </button>
            {user?.role === 'ADMIN' && (
              <button onClick={handleCreateEvent} className="btn-create-event">
                + Create Event
              </button>
            )}
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        {loading ? (
          <div className="loading">Loading events...</div>
        ) : events.length > 0 ? (
          <div className="events-grid">
            {events.map(event => (
              <EventCard key={event.id} event={event} onEventUpdated={fetchEvents} />
            ))}
          </div>
        ) : (
          <div className="no-events">No events found</div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
