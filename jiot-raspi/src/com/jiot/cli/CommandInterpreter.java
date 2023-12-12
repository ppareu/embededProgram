/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiot.cli;

import com.jiot.things.ControlPoint;
import com.jiot.things.ControlPointContainer;
import com.jiot.things.FloatInputSupport;
import com.jiot.things.OutputControlPoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author yjkim
 */
public class CommandInterpreter {
    
    private static final AtomicReference<CommandInterpreter> instance =
            new AtomicReference<CommandInterpreter>();
    
    public static CommandInterpreter getInstance() {
        if (instance.get() == null)
            instance.set(new CommandInterpreter());
        return instance.get();
    }
    
    public interface Command {
        public String execute(String[] args);
        public String getHelp();
    }
    
    private final Map<String, Command> commands = new HashMap<String, Command>();

    private CommandInterpreter() {
        commands.put("list", new Command() {
            @Override
            public String execute(String[] args) {
                StringBuilder sb = new StringBuilder();
                Collection<ControlPoint> cps = 
                    ControlPointContainer.getInstance().getControlPoints();
                sb.append("ControlPointContainer has ")
                  .append(cps.size())
                  .append("'s control points.")
                  .append(System.lineSeparator());
                cps.forEach((cp) -> { 
                    sb.append(cp.toString()).append(System.lineSeparator());
                });
                return sb.toString();
            }

            @Override
            public String getHelp() {
                return "list: display a list of control points in the container";
            }
        });
        commands.put("get", new Command() {
            @Override
            public String execute(String[] args) {
                if (args.length != 2) {
                    return "Invalid get command.";
                } 
                else {
                    ControlPoint cp = getControlPoint(args[1]);
                    if (cp == null) {
                        return "Cannot find the control point(id=" + args[1] + ").";
                    }
                    else {
                        return String.valueOf(cp instanceof FloatInputSupport ? 
                                ((FloatInputSupport)cp).getFloatValue() : cp.getValue());
                    }
                }
            }

            @Override
            public String getHelp() {
                return "get: get the present value of a control point. usage => get point-id(|point-name)";
            }
        });
        commands.put("set", new Command() {
            @Override
            public String execute(String[] args) {
                if (args.length != 3) {
                    return "Invalid set command.";
                } 
                else {
                    ControlPoint cp = getControlPoint(args[1]);
                    if (cp == null) {
                        return "Cannot find the control point(id=" + args[1] + ").";
                    }
                    else if (cp instanceof OutputControlPoint) {
                        int value = Integer.parseInt(args[2]);
                        ((OutputControlPoint)cp).setValue(value);
                        return null;
                    }
                    else {
                        return "The control point(id=" + args[1] + ") is not an output type.";
                    }
                }
            }

            @Override
            public String getHelp() {
                return "set: set the present value of a control point. usage => set point-id(|point-name) value";
            }
        });
        commands.put("rename", new Command() {
            @Override
            public String execute(String[] args) {
                if (args.length != 3) {
                    return "Invalid rename command.";
                } 
                else {
                    ControlPoint cp = getControlPoint(args[1]);
                    if (cp == null) {
                        return "Cannot find the control point(id=" + args[1] + ").";
                    }
                    else {
                        cp.setName(args[2]);
                        return null;
                    }
                }
            }

            @Override
            public String getHelp() {
                return "rename: change the name of a control point. usage => rename point-id(|point-name) new-name";
            }
        });
    }
    
    private ControlPoint getControlPoint(String id) {
        ControlPoint cp = null;
        if (id.matches("[0-9]+") && id.length() >= 1) {
            cp = ControlPointContainer.getInstance().getControlPoint(Integer.parseInt(id));
        }
        else {
            cp = ControlPointContainer.getInstance().getControlPoint(id);
        }
        return cp;
    }
    
    public String help() {
        StringBuilder sb = new StringBuilder();
        sb.append("IoT CLI Commands").append(System.lineSeparator());
        commands.values().forEach((command) -> { 
            sb.append(command.getHelp()).append(System.lineSeparator());
        });
        return sb.toString();
    }
    
    public String execute(String[] args) {
        String response = null;

        if (args.length != 0) {
            if (args[0].equals("help")) {
                response = help();
            }
            else {
                Command cmd = commands.get(args[0]);
                if (cmd != null) {
                    response = cmd.execute(args);
                }
                else {
                    response = "Invalid command: " + args[0];
                }
            }
        }

        return response;
    }
    
    public void register(String name, Command cmd) {
        commands.put(name, cmd);
    }
    
    public void unregister(String name) {
        commands.remove(name);
    }
    
}
