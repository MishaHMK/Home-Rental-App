INSERT INTO bookings (
    id, checkin_date, checkout_date, user_id, accommodation_id, status, is_deleted
) VALUES
      (
          3, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 1, 1, 'PENDING', 0
      );