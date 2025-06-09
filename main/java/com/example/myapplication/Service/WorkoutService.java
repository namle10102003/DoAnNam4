package com.example.myapplication.Service;

import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Domain.Plan;
import com.example.myapplication.Domain.Session;
import com.example.myapplication.Domain.Set;
import com.example.myapplication.Domain.Workout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutService {
    private final ExerciseService exerciseService;
    private final PlanService planService;
    private final SessionService sessionService;
    private final SetService setService;

    private ArrayList<Exercise> listExercise;
    private ArrayList<Plan> listPlan;
    private ArrayList<Session> listSession;
    private ArrayList<Set> listSet;

    public interface ListWorkoutDataListener {
        void onWorkoutsLoaded(List<Workout> workouts);
        void onError(String message);
    }

    public interface WorkoutDataListener {
        void onWorkoutLoaded(Workout workout);
        void onError(String message);
    }

    public WorkoutService() {
        exerciseService = new ExerciseService();
        planService = new PlanService();
        sessionService = new SessionService();
        setService = new SetService();

        listExercise = new ArrayList<>();
        listPlan = new ArrayList<>();
        listSession = new ArrayList<>();
        listSet = new ArrayList<>();
    }

    public void createWorkout(Workout workout, WorkoutDataListener listener) {
        sessionService.getByPlanId(workout.getPlanId(), new SessionService.ListSessionDataListener() {
            @Override
            public void onSessionLoaded(List<Session> sessions) {
                Session existingSession = null;
                for (Session session : sessions) {
                    if (isSameDay(session.getDate(), workout.getDate())) {
                        existingSession = session;
                        break;
                    }
                }

                if (existingSession != null) {
                    // Update sets with existing session ID
                    createSetsWithSession(workout, existingSession.getId(), listener);
                } else {
                    // Create a new session first
                    Session newSession = new Session(0, workout.getPlanId(), workout.getDate());
                    sessionService.createSession(newSession, new SessionService.SessionDataListener() {
                        @Override
                        public void onSessionLoaded(Session createdSession) {
                            createSetsWithSession(workout, createdSession.getId(), listener);
                        }

                        @Override
                        public void onError(String message) {
                            listener.onError(message);
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });
    }

    private void createSetsWithSession(Workout workout, int sessionId, WorkoutDataListener listener) {
        List<Set> sets = workout.getSets();
        int total = sets.size();
        int[] updatedCount = {0};

        for (Set set : sets) {
            set.setSessionId(sessionId);
            setService.createSet(set, new SetService.SetDataListener() {
                @Override
                public void onSetLoaded(Set updatedSet) {
                    updatedCount[0]++;
                    if (updatedCount[0] == total) {
                        listener.onWorkoutLoaded(workout); // Notify when all sets are done
                    }
                }

                @Override
                public void onError(String message) {
                    listener.onError(message); // Fail fast
                }
            });
        }
    }

    public void updateWorkout(Workout workout, WorkoutDataListener listener) {
        sessionService.getByPlanId(workout.getPlanId(), new SessionService.ListSessionDataListener() {
            @Override
            public void onSessionLoaded(List<Session> sessions) {
                Session existingSession = null;
                for (Session session : sessions) {
                    if (isSameDay(session.getDate(), workout.getDate())) {
                        existingSession = session;
                        break;
                    }
                }

                if (existingSession != null) {
                    // Update sets with existing session ID
                    updateSetsWithSession(workout, existingSession.getId(), listener);
                } else {
                    // Create a new session first
                    Session newSession = new Session(0, workout.getPlanId(), workout.getDate());
                    sessionService.createSession(newSession, new SessionService.SessionDataListener() {
                        @Override
                        public void onSessionLoaded(Session createdSession) {
                            updateSetsWithSession(workout, createdSession.getId(), listener);
                        }

                        @Override
                        public void onError(String message) {
                            listener.onError(message);
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });
    }

    private void updateSetsWithSession(Workout workout, int sessionId, WorkoutDataListener listener) {
        List<Set> sets = workout.getSets();
        int total = sets.size();
        int[] updatedCount = {0};

        for (Set set : sets) {
            set.setSessionId(sessionId);
            setService.updateSet(set, new SetService.SetDataListener() {
                @Override
                public void onSetLoaded(Set updatedSet) {
                    updatedCount[0]++;
                    if (updatedCount[0] == total) {
                        listener.onWorkoutLoaded(workout); // Notify when all sets are done
                    }
                }

                @Override
                public void onError(String message) {
                    listener.onError(message); // Fail fast
                }
            });
        }
    }

    public void deleteWorkout(Workout workout, WorkoutDataListener listener) {
        List<Set> sets = workout.getSets();
        int total = sets.size();
        int[] deleteCount = {0};

        for (Set set : sets) {
            setService.deleteSet(set.getId(), new SetService.ListSetDataListener() {
                @Override
                public void onSetsLoaded(List<Set> sets) {
                    deleteCount[0]++;
                    if (deleteCount[0] == total) {
                        listener.onWorkoutLoaded(workout);
                    }
                }

                @Override
                public void onError(String message) {
                    listener.onError(message);
                }
            });
        }
    }

    public void getByUserId(int userId, ListWorkoutDataListener listener) {
        exerciseService.GetAll(new ExerciseService.ListExerciseDataListener() {
            @Override
            public void onExerciseLoaded(List<Exercise> exercises) {
                listExercise.clear();
                listExercise.addAll(exercises);

                planService.getPlansByUserId(userId, new PlanService.ListPlanDataListener() {
                    @Override
                    public void onPlansLoaded(List<Plan> plans) {
                        listPlan.clear();
                        listPlan.addAll(plans);

                        fetchSessionsAndSets(plans, listener);
                    }

                    @Override
                    public void onError(String message) {
                        listener.onError(message);
                    }
                });
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });
    }

    private void fetchSessionsAndSets(List<Plan> plans, ListWorkoutDataListener listener) {
        listSession.clear();
        listSet.clear();

        if (plans.isEmpty()) {
            listener.onWorkoutsLoaded(new ArrayList<>());
            return;
        }

        final int[] sessionCounter = {0};

        for (Plan plan : plans) {
            sessionService.getByPlanId(plan.getId(), new SessionService.ListSessionDataListener() {
                @Override
                public void onSessionLoaded(List<Session> sessions) {
                    listSession.addAll(sessions);

                    if (sessions.isEmpty()) {
                        checkIfAllSessionsLoaded(plans.size(), ++sessionCounter[0], listener);
                    }

                    final int[] setCounter = {0};

                    for (Session session : sessions) {
                        setService.getBySessionId(session.getId(), new SetService.ListSetDataListener() {
                            @Override
                            public void onSetsLoaded(List<Set> sets) {
                                listSet.addAll(sets);
                                setCounter[0]++;
                                if (setCounter[0] == sessions.size()) {
                                    checkIfAllSessionsLoaded(plans.size(), ++sessionCounter[0], listener);
                                }
                            }

                            @Override
                            public void onError(String message) {
                                listener.onError(message);
                            }
                        });
                    }
                }

                @Override
                public void onError(String message) {
                    listener.onError(message);
                }
            });
        }
    }

    private void checkIfAllSessionsLoaded(int totalPlans, int loadedCount, ListWorkoutDataListener listener) {
        if (loadedCount >= totalPlans) {
            // All sessions and sets are loaded
            List<Workout> workouts = buildWorkouts(listExercise, listPlan, listSession, listSet);
            listener.onWorkoutsLoaded(workouts);
        }
    }

    private List<Workout> buildWorkouts(List<Exercise> exercises, List<Plan> plans, List<Session> sessions, List<Set> sets) {
        List<Workout> result = new ArrayList<>();

        for (Session session : sessions) {
            // Get the plan for this session
            Plan plan = null;
            for (Plan p : plans) {
                if (p.getId() == session.getPlanId()) {
                    plan = p;
                    break;
                }
            }

            if (plan == null) continue;

            // Group sets by exerciseId for this session
            Map<Integer, ArrayList<Set>> exerciseSetMap = new HashMap<>();
            for (Set set : sets) {
                if (set.getSessionId() == session.getId()) {
                    exerciseSetMap
                            .computeIfAbsent(set.getExerciseId(), k -> new ArrayList<>())
                            .add(set);
                }
            }

            // For each exerciseId used in this session, build a Workout
            for (Map.Entry<Integer, ArrayList<Set>> entry : exerciseSetMap.entrySet()) {
                int exerciseId = entry.getKey();
                ArrayList<Set> exerciseSets = entry.getValue();

                // Get the exercise object
                Exercise exercise = null;
                for (Exercise ex : exercises) {
                    if (ex.getId() == exerciseId) {
                        exercise = ex;
                        break;
                    }
                }

                if (exercise == null) continue;

                Workout workout = new Workout(
                        plan.getId(),
                        session.getId(),
                        exerciseId,
                        plan.getName(),
                        session.getDate(),
                        exercise,
                        exerciseSets
                );

                result.add(workout);
            }
        }

        return result;
    }

    private boolean isSameDay(Date date1, Date date2) {
        // Implement a method to check if two Date objects represent the same day
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }
}
