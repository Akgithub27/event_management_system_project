import React, { useEffect, useState, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { eventService, registrationService } from '../services/api';
import { AuthContext } from '../context/AuthContext';
import '../styles/EventDetail.css';

export const EventDetail = () => {
  const { id } = useParams();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [registering, setRegistering] = useState(false);
  const navigate = useNavigate();
  const { isAuthenticated } = useContext(AuthContext);

  useEffect(() => {
    fetchEventDetail();
  }, [id]);

  const fetchEventDetail = async () => {
    try {
      setLoading(true);
      const response = await eventService.getEventById(id);
      setEvent(response.data.data);
    } catch (err) {
      setError('Failed to load event details');
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    try {
      setRegistering(true);
      await registrationService.registerForEvent(id);
      setError('');
      alert('Successfully registered for the event!');
      fetchEventDetail();
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setRegistering(false);
    }
  };

  const handleCancel = async () => {
    if (window.confirm('Are you sure you want to cancel your registration?')) {
      try {
        setRegistering(true);
        await registrationService.cancelRegistration(id);
        setError('');
        alert('Registration cancelled successfully');
        fetchEventDetail();
      } catch (err) {
        setError(err.response?.data?.message || 'Cancellation failed');
      } finally {
        setRegistering(false);
      }
    }
  };

  if (loading) return <div className="loading-container"><div className="loading">Loading event details...</div></div>;
  if (!event) return <div className="loading-container"><div className="error-message">Event not found</div></div>;

  const spotsAvailable = event.capacity - event.registeredCount;

  return (
    <div className="event-detail-container">
      <button onClick={() => navigate('/dashboard')} className="btn-back">← Back to Dashboard</button>

      <div className="event-detail-card">
        <div className="event-header">
          <h1>{event.title}</h1>
          <div className="event-badge">
            {event.capacity > event.registeredCount ? (
              <span className="badge-available">Available</span>
            ) : (
              <span className="badge-full">Full</span>
            )}
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="event-info">
          <div className="info-row">
            <strong>Date & Time:</strong>
            <span>{new Date(event.eventDate).toLocaleString()}</span>
          </div>
          <div className="info-row">
            <strong>Venue:</strong>
            <span>{event.venue}</span>
          </div>
          <div className="info-row">
            <strong>Category:</strong>
            <span>{event.category}</span>
          </div>
          <div className="info-row">
            <strong>Capacity:</strong>
            <span>{event.registeredCount}/{event.capacity} ({spotsAvailable} spots available)</span>
          </div>
          <div className="info-row">
            <strong>Organized by:</strong>
            <span>{event.createdByName}</span>
          </div>
        </div>

        <div className="event-description">
          <h3>Description</h3>
          <p>{event.description}</p>
        </div>

        {event.speakers && event.speakers.length > 0 && (
          <div className="event-speakers">
            <h3>Speakers</h3>
            <div className="speakers-list">
              {event.speakers.map(speaker => (
                <div key={speaker.id} className="speaker-card">
                  <h4>{speaker.name}</h4>
                  <p>{speaker.bio}</p>
                  {speaker.expertise && <p><strong>Expertise:</strong> {speaker.expertise}</p>}
                </div>
              ))}
            </div>
          </div>
        )}

        <div className="event-actions">
          {isAuthenticated ? (
            event.isRegistered ? (
              <button
                onClick={handleCancel}
                disabled={registering}
                className="btn-cancel"
              >
                {registering ? 'Cancelling...' : 'Cancel Registration'}
              </button>
            ) : spotsAvailable > 0 ? (
              <button
                onClick={handleRegister}
                disabled={registering}
                className="btn-register"
              >
                {registering ? 'Registering...' : 'Register for Event'}
              </button>
            ) : (
              <button disabled className="btn-register btn-disabled">
                Event Full
              </button>
            )
          ) : (
            <button onClick={() => navigate('/login')} className="btn-register">
              Login to Register
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default EventDetail;
