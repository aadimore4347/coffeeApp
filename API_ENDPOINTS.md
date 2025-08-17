# Coffee Management System API Endpoints

This document lists all available REST API endpoints for the Coffee Management System.

## Base URL
All endpoints are prefixed with the base URL of your application (e.g., `http://localhost:8080`)

---

## User Management (`/api/users`)

### CRUD Operations
- `POST /api/users` - Create new user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (soft delete)

### Query Operations
- `GET /api/users` - Get all users
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/role/{role}` - Get users by role
- `GET /api/users/facility` - Get facility users
- `GET /api/users/admin` - Get admin users
- `GET /api/users/recent?hours={hours}` - Get recently created users

### Utility Operations
- `PUT /api/users/{id}/reactivate` - Reactivate user
- `GET /api/users/{id}/exists` - Check if user exists
- `GET /api/users/username/{username}/exists` - Check if username exists

---

## Facility Management (`/api/facilities`)

### CRUD Operations
- `POST /api/facilities` - Create new facility
- `GET /api/facilities/{id}` - Get facility by ID
- `PUT /api/facilities/{id}` - Update facility
- `DELETE /api/facilities/{id}` - Delete facility (soft delete)

### Query Operations
- `GET /api/facilities` - Get all facilities
- `GET /api/facilities/name/{name}` - Get facility by name
- `GET /api/facilities/location/{location}` - Get facilities by location
- `GET /api/facilities/search/location?keyword={keyword}` - Search facilities by location
- `GET /api/facilities/with-active-machines` - Get facilities with active machines
- `GET /api/facilities/with-low-supply-machines` - Get facilities with low supply machines
- `GET /api/facilities/{id}/with-machines` - Get facility with machine details
- `GET /api/facilities/recent?hours={hours}` - Get recently created facilities
- `GET /api/facilities/machine-count?minCount={min}&maxCount={max}` - Get facilities by machine count

### Utility Operations
- `PUT /api/facilities/{id}/reactivate` - Reactivate facility
- `GET /api/facilities/{id}/exists` - Check if facility exists
- `GET /api/facilities/name/{name}/exists` - Check if facility name exists

---

## Coffee Machine Management (`/api/machines`)

### CRUD Operations
- `POST /api/machines` - Create new coffee machine
- `GET /api/machines/{id}` - Get machine by ID
- `PUT /api/machines/{id}` - Update machine
- `DELETE /api/machines/{id}` - Delete machine (soft delete)

### Query Operations
- `GET /api/machines` - Get all machines
- `GET /api/machines/facility/{facilityId}` - Get machines by facility
- `GET /api/machines/status/{status}` - Get machines by status
- `GET /api/machines/operational` - Get operational machines
- `GET /api/machines/low-supplies` - Get machines with low supplies
- `GET /api/machines/critical-supplies` - Get machines with critical supplies
- `GET /api/machines/maintenance-needed` - Get machines needing maintenance

### Control Operations
- `PUT /api/machines/{id}/levels` - Update machine supply levels
- `PUT /api/machines/{id}/status` - Update machine status
- `PUT /api/machines/{id}/turn-on` - Turn machine on
- `PUT /api/machines/{id}/turn-off` - Turn machine off
- `PUT /api/machines/{id}/refill` - Refill all machine supplies

### Brewing Operations
- `POST /api/machines/brew` - Brew coffee

---

## Usage History (`/api/usage-history`)

### CRUD Operations
- `POST /api/usage-history` - Create usage record
- `POST /api/usage-history/simple` - Create usage record with simple parameters
- `GET /api/usage-history/{id}` - Get usage record by ID
- `DELETE /api/usage-history/{id}` - Delete usage record

### Query Operations
- `GET /api/usage-history` - Get all usage records
- `GET /api/usage-history/machine/{machineId}` - Get usage by machine
- `GET /api/usage-history/user/{userId}` - Get usage by user
- `GET /api/usage-history/brew-type/{brewType}` - Get usage by brew type
- `GET /api/usage-history/date-range` - Get usage by date range
- `GET /api/usage-history/machine/{machineId}/date-range` - Get usage by machine and date range
- `GET /api/usage-history/today` - Get today's usage
- `GET /api/usage-history/machine/{machineId}/today` - Get today's usage by machine
- `GET /api/usage-history/recent?hours={hours}` - Get recent usage

### Analytics Operations
- `GET /api/usage-history/stats/machine/{machineId}` - Get usage statistics by machine
- `GET /api/usage-history/stats/user/{userId}` - Get usage statistics by user
- `GET /api/usage-history/stats/brew-types` - Get brew type popularity
- `GET /api/usage-history/stats/hourly-patterns` - Get hourly usage patterns

---

## Alert Management (`/api/alerts`)

### Alert Creation
- `POST /api/alerts` - Create generic alert
- `POST /api/alerts/low-water` - Create low water alert
- `POST /api/alerts/low-milk` - Create low milk alert
- `POST /api/alerts/low-beans` - Create low beans alert
- `POST /api/alerts/malfunction` - Create malfunction alert
- `POST /api/alerts/offline` - Create offline alert
- `POST /api/alerts/bulk` - Create bulk alerts

### Query Operations
- `GET /api/alerts/{id}` - Get alert by ID
- `GET /api/alerts/active` - Get all active alerts
- `GET /api/alerts/machine/{machineId}` - Get alerts by machine
- `GET /api/alerts/type/{alertType}` - Get alerts by type
- `GET /api/alerts/recent?hours={hours}` - Get recent alerts
- `GET /api/alerts/critical` - Get critical alerts
- `GET /api/alerts/supply` - Get supply alerts
- `GET /api/alerts/today` - Get today's alerts
- `GET /api/alerts/unresolved?hours={hours}` - Get unresolved alerts
- `GET /api/alerts/attention-required` - Get alerts requiring attention

### Alert Resolution
- `PUT /api/alerts/{id}/resolve` - Resolve alert by ID
- `PUT /api/alerts/resolve/machine/{machineId}/type/{alertType}` - Resolve alerts by machine and type

### Utility Operations
- `GET /api/alerts/duplicate-check` - Check for duplicate alerts
- `GET /api/alerts/stats` - Get alert statistics
- `GET /api/alerts/stats/machine/{machineId}` - Get alert statistics by machine

---

## Dashboard & Analytics (`/api/dashboard`)

### Dashboard Views
- `GET /api/dashboard/admin` - Get admin dashboard summary
- `GET /api/dashboard/facility/{facilityId}` - Get facility dashboard summary
- `GET /api/dashboard/health` - Get system health overview

### Analytics
- `GET /api/dashboard/analytics/usage` - Get usage analytics
- `GET /api/dashboard/analytics/machines` - Get machine performance analytics
- `GET /api/dashboard/analytics/alerts` - Get alert analytics

---

## Request/Response Formats

### Common Request Headers
```
Content-Type: application/json
Accept: application/json
```

### Common Response Status Codes
- `200 OK` - Successful GET request
- `201 Created` - Successful POST request
- `204 No Content` - Successful DELETE request
- `400 Bad Request` - Invalid request data
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

### Sample Request Bodies

#### Create User
```json
{
  "username": "john.doe",
  "role": "FACILITY"
}
```

#### Create Facility
```json
{
  "name": "Main Office",
  "location": "New York, NY"
}
```

#### Create Coffee Machine
```json
{
  "facilityId": "facility-123",
  "status": "ON",
  "temperature": 85.0,
  "waterLevel": 100.0,
  "milkLevel": 100.0,
  "beansLevel": 100.0
}
```

#### Brew Coffee
```json
{
  "machineId": "machine-123",
  "brewType": "LATTE",
  "userId": "user-123",
  "size": 1.0,
  "strength": 1.0,
  "milkRatio": 0.5,
  "temperature": 80.0
}
```

---

## Error Handling

All endpoints return appropriate HTTP status codes and error messages in JSON format:

```json
{
  "error": "Error description",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

---

## Authentication & Authorization

This API currently uses basic CORS configuration allowing all origins. In production, implement proper authentication and authorization mechanisms.

---

## Testing

The API can be tested using tools like:
- Postman
- cURL
- Swagger UI (if configured)
- Any HTTP client

Example cURL command:
```bash
curl -X GET "http://localhost:8080/api/users" \
  -H "Content-Type: application/json"
```