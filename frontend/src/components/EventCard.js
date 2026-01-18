import React, { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/EventCard.css';

const EventCard = ({ event, onEventUpdated }) => {
  const navigate = useNavigate();
  const spotsAvailable = event.capacity - event.registeredCount;
  const spotsPercentage = (event.registeredCount / event.capacity) * 100;

  const handleViewDetails = () => {
    navigate(`/event/${event.id}`);
  };

  return (
    <div className="event-card" onClick={handleViewDetails}>
      <div className="event-card-header">
        <h3>{event.title}</h3>
        <span className="event-category">{event.category}</span>
      </div>

      <div className="event-card-body">
        <p className="event-date">
          📅 {new Date(event.eventDate).toLocaleDateString()} at {new Date(event.eventDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
        </p>
        <p className="event-venue">
          📍 {event.venue}
        </p>
        <p className="event-organizer">
          👤 {event.createdByName}
        </p>

        <div className="capacity-info">
          <div className="capacity-bar">
            <div
              className="capacity-fill"
              style={{ width: `${Math.min(spotsPercentage, 100)}%` }}
            ></div>
          </div>
          <p className="capacity-text">
            {event.registeredCount}/{event.capacity} registered
            {spotsAvailable > 0 && ` • ${spotsAvailable} spots available`}
          </p>
        </div>

        <p className="event-description">
          {event.description.length > 100
            ? event.description.substring(0, 100) + '...'
            : event.description}
        </p>
      </div>

      <div className="event-card-footer">
        <button className="btn-view-details">View Details</button>
      </div>
    </div>
  );
};

export default EventCard;
