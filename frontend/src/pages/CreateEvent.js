import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { eventService } from '../services/api';
import { AuthContext } from '../context/AuthContext';
import '../styles/CreateEvent.css';

export const CreateEvent = () => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    eventDate: '',
    venue: '',
    category: 'Tech',
    capacity: '100'
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);

  // Check if user is admin
  if (user?.role !== 'ADMIN') {
    return (
      <div className="access-denied">
        <h2>Access Denied</h2>
        <p>Only administrators can create events.</p>
        <button onClick={() => navigate('/dashboard')} className="btn-primary">Back to Dashboard</button>
      </div>
    );
  }

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const eventDate = new Date(formData.eventDate).toISOString();
      await eventService.createEvent({
        ...formData,
        eventDate,
        capacity: parseInt(formData.capacity)
      });
      
      navigate('/dashboard', { state: { message: 'Event created successfully!' } });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create event');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="create-event-container">
      <button onClick={() => navigate('/dashboard')} className="btn-back">← Back</button>

      <div className="create-event-card">
        <h2>Create New Event</h2>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="title">Event Title *</label>
            <input
              id="title"
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              required
              placeholder="Enter event title"
            />
          </div>

          <div className="form-group">
            <label htmlFor="description">Description *</label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              required
              placeholder="Event description"
              rows="5"
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="eventDate">Event Date & Time *</label>
              <input
                id="eventDate"
                type="datetime-local"
                name="eventDate"
                value={formData.eventDate}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="capacity">Capacity *</label>
              <input
                id="capacity"
                type="number"
                name="capacity"
                value={formData.capacity}
                onChange={handleChange}
                required
                min="1"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="venue">Venue *</label>
              <input
                id="venue"
                type="text"
                name="venue"
                value={formData.venue}
                onChange={handleChange}
                required
                placeholder="Event location"
              />
            </div>

            <div className="form-group">
              <label htmlFor="category">Category *</label>
              <select
                id="category"
                name="category"
                value={formData.category}
                onChange={handleChange}
                required
              >
                <option value="Tech">Tech</option>
                <option value="Business">Business</option>
                <option value="Workshop">Workshop</option>
                <option value="Networking">Networking</option>
                <option value="Other">Other</option>
              </select>
            </div>
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={() => navigate('/dashboard')}
              className="btn-cancel"
            >
              Cancel
            </button>
            <button type="submit" disabled={loading} className="btn-primary">
              {loading ? 'Creating...' : 'Create Event'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateEvent;
