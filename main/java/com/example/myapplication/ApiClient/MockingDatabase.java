package com.example.myapplication.ApiClient;

import com.example.myapplication.Domain.BodyTrack;
import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Domain.Plan;
import com.example.myapplication.Domain.Session;
import com.example.myapplication.Domain.Set;
import com.example.myapplication.Domain.User;
import com.example.myapplication.Enum.MuscleEnum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MockingDatabase {
    private static MockingDatabase instance;

    public ArrayList<User> users;
    public ArrayList<Exercise> exercises;
    public ArrayList<BodyTrack> bodyTracks;
    public ArrayList<Plan> plans;
    public ArrayList<Session> sessions;
    public ArrayList<Set> sets;

    private MockingDatabase() {
        users = generateUsers();
        exercises = generateExercises();
        bodyTracks = generateBodyTracks();
        plans = generatePlans();
        sessions = generateSessions();
        sets = generateSets();
    }

    public static synchronized MockingDatabase getInstance() {
        if (instance == null) {
            instance = new MockingDatabase();
        }
        return instance;
    }

    private ArrayList<User> generateUsers() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("1", "nam", "Pass1234",
                "Le Nguyen Nhat Nam", "namle@gmail.com",
                "099912345", "1999-01-01", "Male"));
        users.add(new User("2", "long", "Pass1234",
                "Nguyen Hoang Long", "longnguyen@gmail.com",
                "099912345", "1999-01-11", "Male"));
        return users;
    }

    private ArrayList<Exercise> generateExercises() {
        ArrayList<Exercise> exerciseList = new ArrayList<>();

        exerciseList.add(new Exercise(
                1,
                "Seated Dumbbell Shoulder Press",
                "https://www.youtube.com/watch?v=B-aVuyhvLHU",
                "A strength exercise that targets the shoulders using dumbbells while seated.",
                MuscleEnum.SHOULDERS,
                MuscleEnum.TRICEPS,
                null
        ));

        exerciseList.add(new Exercise(
                2,
                "Band Chest Fly",
                "https://www.youtube.com/watch?v=JA8lXeb9yEo",
                "An isolation chest movement using resistance bands to target the pectoral muscles.",
                MuscleEnum.CHEST,
                MuscleEnum.SHOULDERS,
                null
        ));

        exerciseList.add(new Exercise(
                3,
                "Yoga Warrior I",
                "https://www.youtube.com/watch?v=Q9vQ3Vxg3IQ",
                "A foundational yoga pose that strengthens the legs, opens the hips, and improves balance.",
                MuscleEnum.QUADS,
                MuscleEnum.GLUTES,
                MuscleEnum.CALVES
        ));


        return exerciseList;
    }

    private ArrayList<BodyTrack> generateBodyTracks() {
        ArrayList<BodyTrack> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            BodyTrack track = new BodyTrack();
            track.Id = i;
            track.Weight = 60 + i;
            track.Height = 170 + (i % 3);
            track.Date = new Date(System.currentTimeMillis() - i * 86400000L); // subtract i days
            track.UserId = 1;
            list.add(track);
        }
        return list;
    }

    private ArrayList<Plan> generatePlans() {
        ArrayList<Plan> planList = new ArrayList<>();

        planList.add(new Plan(1, 1, "Loss weight"));
        planList.add(new Plan(2, 1, "Build muscle"));

        planList.add(new Plan(3, 2, "Yoga"));

        return planList;
    }

    private ArrayList<Session> generateSessions() {
        ArrayList<Session> sessionList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        Date nextDay = calendar.getTime();

        sessionList.add(new Session(1, 1, new Date()));
        sessionList.add(new Session(2, 2, new Date()));
        sessionList.add(new Session(3, 1, nextDay));


        sessionList.add(new Session(4, 3, new Date()));

        return sessionList;
    }

    private ArrayList<Set> generateSets() {
        ArrayList<Set> setList = new ArrayList<>();

        //Today sets of user 1
        setList.add(new Set(1, 1, 1, 10, 12, 3));
        setList.add(new Set(2, 1, 1, 15, 12, 3));
        setList.add(new Set(3, 1, 1, 20, 12, 3));

        setList.add(new Set(4, 1, 2, 10, 12, 3));
        setList.add(new Set(5, 1, 2, 15, 12, 3));
        setList.add(new Set(6, 1, 2, 20, 12, 3));

        setList.add(new Set(7, 2, 3, 0, 12, 3));
        setList.add(new Set(8, 2, 3, 0, 12, 3));
        setList.add(new Set(9, 2, 3, 0, 12, 3));

        //Tomorrow sets of user 1
        setList.add(new Set(10, 3, 1, 10, 12, 3));
        setList.add(new Set(11, 3, 1, 15, 12, 3));
        setList.add(new Set(12, 3, 1, 20, 12, 3));

        //Today sets of user 1
        setList.add(new Set(13, 4, 3, 0, 12, 3));
        setList.add(new Set(14, 4, 3, 0, 12, 3));
        setList.add(new Set(15, 4, 3, 0, 12, 3));

        return setList;
    }
}

