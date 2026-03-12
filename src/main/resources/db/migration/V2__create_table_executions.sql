create table executions (
   id UUID PRIMARY KEY,
   user_id UUID NOT NULL,
   target VARCHAR(255) NOT NULL,
   instruction TEXT NOT NULL,
   status VARCHAR(50) NOT NULL,
   start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   end_time TIMESTAMP,
   path_log_file VARCHAR(255) NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (user_id) REFERENCES users(id)
);