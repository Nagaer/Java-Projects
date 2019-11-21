package com.reagan.ind.Zerus;

import java.util.*;


public class Creature {
    //int hits; //Здоровье существа, опустится до 0 - погибнет
    int energy; //Энергия существа, опустится до 0 - начнёт отбавлять HP
    int cur; //Текущая команда для выполнения, указатель
    int status; //Состояние существа. 0 - не существует, 1 - жив, 2 - труп
    int x, y; //Координаты существа
    List<Integer> color;
    MarkovChain mind; //Мозг - однонаправленный граф состояний

    void step() {
        Random random = new Random();
        if (status == 0 || status == 2) return; //Если мы не существуем или мертвы, ничего не делаем
        cur = 0;
        while (cur != -1) {
            execute(cur);
            cur = mind.branching(cur);
        }
        if (energy > 1000) {
            mitosis();
        }
        if (energy < 0) {
            status = 2;
        }
    }

    private void execute(int num) {
        switch (num) {
            case 20:
                photosynthesis();
                //cur = -1;
                break;
        }
    }

    private String surrounded() {
        List<String> directions = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"));
        if (x==0) {
            directions.remove("6");
            directions.remove("7");
            directions.remove("8");
        }
        if (y==0) {
            directions.remove("8");
            directions.remove("1");
            directions.remove("2");
        }
        if (x==Environment.width*4) {
            directions.remove("2");
            directions.remove("3");
            directions.remove("4");
        }
        if (y==Environment.height*4) {
            directions.remove("4");
            directions.remove("5");
            directions.remove("6");
        }
        if (directions.contains("1"))
            if (Environment.creatures[x][y-1] != null)
                directions.remove("1");
        if (directions.contains("2"))
            if (Environment.creatures[x+1][y-1] != null)
                directions.remove("2");
        if (directions.contains("3"))
            if (Environment.creatures[x+1][y] != null)
                directions.remove("3");
        if (directions.contains("4"))
            if (Environment.creatures[x+1][y+1] != null)
                directions.remove("4");
        if (directions.contains("5"))
            if (Environment.creatures[x][y+1] != null)
                directions.remove("5");
        if (directions.contains("6"))
            if (Environment.creatures[x-1][y+1] != null)
                directions.remove("6");
        if (directions.contains("7"))
            if (Environment.creatures[x-1][y] != null)
                directions.remove("7");
        if (directions.contains("8"))
            if (Environment.creatures[x-1][y-1] != null)
                directions.remove("8");
        if (directions.size() == 0) {
            return "0";
        }
        int rnd = new Random().nextInt(directions.size());
        return directions.get(rnd);
    }

    private void photosynthesis() {
        double q = 11 - (15*y/Environment.height)+50;
        energy += q; //Ускоренный рост для тестов
    }

    private void mitosis() {
        Creature newCreature = new Creature();
        int incX = 0, incY = 0;
        switch (surrounded()) {
            case "0":
                status = 2;
                return;
            case "1":
                incX = 0;
                incY = -1;
                break;
            case "2":
                incX = 1;
                incY = -1;
                break;
            case "3":
                incX = 1;
                incY = 0;
                break;
            case "4":
                incX = 1;
                incY = 1;
                break;
            case "5":
                incX = 0;
                incY = 1;
                break;
            case "6":
                incX = -1;
                incY = 1;
                break;
            case "7":
                incX = -1;
                incY = 0;
                break;
            case "8":
                incX = -1;
                incY = -1;
                break;
        }
        this.energy /= 2;
        newCreature.cur = 0;
        newCreature.x = this.x + incX;
        newCreature.y = this.y + incY;
        newCreature.energy = this.energy/2;
        newCreature.color = this.color;
        newCreature.status = 1;
        newCreature.mind = this.mind.mutate();
        this.mind = this.mind.mutate();

        Environment.creatures[newCreature.x][newCreature.y] = newCreature;
    }
}
