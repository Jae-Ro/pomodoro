import React, { Component } from 'react';
import Paper from '@material-ui/core/Paper';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
// import TablePagination from '@material-ui/core/TablePagination';
import TableRow from '@material-ui/core/TableRow';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Typography from '@material-ui/core/Typography';

// import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import Grid from '@material-ui/core/Grid';

// import FormGroup from '@material-ui/core/FormGroup';
// import TextField from '@material-ui/core/TextField';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
// import FormHelperText from '@material-ui/core/FormHelperText';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import Input from '@material-ui/core/Input';

import AddIcon from '@material-ui/icons/Add';
import Fab from '@material-ui/core/Fab';

import ProjectModal from '../components/ProjectModal';
import { connect } from 'react-redux';
import axios from 'axios';
import moment from 'moment';
// import Link from '@material-ui/core/Link';


import '../styles/userDashboard.scss';
import { actionCreators } from '../models/actions/projectModalActions';

class UserDashboard extends Component {
    constructor(props) {
        super(props);
        this.state = {
            tableData: [],
            deleteAlert: false,
            deleteId: 0,
            startPom: false,
            timerInterval: null,
            timeLeft: 1500000,
            maxMilliseconds: 1000,
            maxSeconds: 60,
            maxMinutes: 25,
            completedPomodoros: 0,
            associatePomAlert: false,
            pomAssociationProject: "",
            pomSessionStop: false,
            sessionStart: null,
            sessionEnd: null
        };
        this.userId = (this.props.loginPage.userId) ? this.props.loginPage.userId : this.props.loginPage.state.userId;
        this.sessionStartTime = null;
    }
    isMobileDevice() {
        return (navigator.userAgent.includes("Android") || navigator.userAgent.includes("iPhone"));
    }
    componentDidMount() {
        // setInterval(() => console.log("hi"), 0.000001);
        // console.log(this.props.loginPage);
        axios.get(process.env.REACT_APP_API_URL + `/users/${this.userId}/projects`)
            .then(res => {
                this.setState({ tableData: res.data });
            })
            .catch(e => console.log(e));
    }
    componentDidUpdate(prevProps, prevState) {
        setTimeout(() => {
            if ((prevProps.projectModal.projectModalOpen === true && this.props.projectModal.projectModalOpen === false) || (prevState.deleteAlert === true && this.state.deleteAlert === false)) {
                // console.log('getting data now');
                axios.get(process.env.REACT_APP_API_URL + `/users/${this.userId}/projects`)
                    .then(res => {
                        // console.log(res);
                        if (JSON.stringify(this.state.tableData) !== JSON.stringify(res.data)) this.setState({ tableData: res.data });
                    })
                    .catch(e => console.log(e));
            }
        }
            , 500);
    }
    onEdit(id) {
        let foundProject = null;
        for (let project of this.state.tableData) {
            if (project.id === id) {
                foundProject = project;
                console.log("FOUND IT: ", foundProject);
            }
        }
        this.props.editProjectModal(foundProject);
    };
    deleteRequest(id) {
        axios.delete(process.env.REACT_APP_API_URL + `/users/${this.userId}/projects/${id}`)
            .then(resp => {
                console.log(resp);
            })
            .catch(e => console.log(e));
    }
    onDelete(id) {
        let foundId = null;
        for (let project of this.state.tableData) {
            if (project.id === id) {
                foundId = id;
                console.log("FOUND IT: ", foundId);
            }
        }
        this.setState({ deleteAlert: true, deleteId: foundId });
    };
    onDeleteAlertAgreement = () => {
        this.deleteRequest(this.state.deleteId);
        this.props.deleteProjectModal(this.state.deleteId);
        this.setState({ deleteAlert: false, deleteId: 0 });

    };
    onDeleteAlertCancel = () => {
        this.setState({ deleteAlert: false, deleteId: 0 });
    };
    checkStartStop() {
        if (!this.state.startPom && this.state.timeLeft === 1500000) {
            this.setState({ associatePomAlert: true });
        } else {
            this.startStopTimer();
        }
    }
    startStopTimer() {
        if (!this.state.startPom) {
            console.log('start');
            if (!this.sessionStartTime) this.sessionStartTime = moment().format();
            this.setState({
                startPom: !this.state.startPom,
                timerInterval: setInterval(() => {
                    this.decreaseTimer();
                    this.formatTime();
                    // console.log(this.state.timeLeft);
                }, 1)
            });
        } else {
            console.log("stop");
            clearInterval(this.state.timerInterval);
            this.setState({ startPom: !this.state.startPom });

        }
    }
    resetTimer() {
        console.log("reset - ", this.state.pomAssociationProject);
        if (this.state.pomAssociationProject !== "None" && this.state.pomAssociationProject !== "") {
            clearInterval(this.state.timerInterval);
            this.setState({ startPom: false, pomSessionStop: true });
        } else {
            clearInterval(this.state.timerInterval);
            const endTime = moment().format();
            console.log("timeLeft: ", this.state.timeLeft, "pomodorosCompleted: ", this.state.completedPomodoros, "sessionStart: ", this.sessionStartTime, "sessionEnd: ", endTime);
            this.sessionStartTime = null;
            this.setState({ timeLeft: 1500000, startPom: false, pomAssociationProject: "", sessionEnd: endTime });
        }
    }
    decreaseTimer() {
        if (this.state.timeLeft > 0) this.setState({ timeLeft: this.state.timeLeft - 1 });
        else this.setState({ timeLeft: 1500000, completedPomodoros: this.state.completedPomodoros + 1 });
    }
    formatTime() {
        // if (this.state.maxMilliseconds > 0)
        //     this.setState({ maxMilliseconds: this.state.maxMilliseconds - 1 });
        // else if (this.state.maxMilliseconds === 0 && this.state.maxSeconds > 0 && this.state.maxMinutes > 0)
        //     this.setState({ maxMilliseconds: 1000, maxSeconds: this.state.maxSeconds - 1 });
        // else if (this.state.maxSeconds === 0)
        //     this.setState({ maxMilliseconds: 1000, maxSeconds: 60, maxMinutes: this.state.maxMinutes - 1 });
    }
    onPomAssociateAgree = () => {
        if (this.state.pomAssociationProject !== "None" && this.state.pomAssociationProject !== "") {
            // Associate the pomodoro with a the project selected
            let selection = this.state.pomAssociationProject;

            this.startStopTimer();
        } else {
            this.startStopTimer();
        }
        this.setState({ associatePomAlert: false });
    };
    onPomAssociateCancel = () => {
        this.setState({ associatePomAlert: false });
    };
    onPomSessionLoggingAgree = () => {
        // record end of session by making a post request
        const endTime = moment().format();
        console.log("timeLeft: ", this.state.timeLeft, "pomodorosCompleted: ", this.state.completedPomodoros, "sessionStart: ", this.sessionStartTime, "sessionEnd: ", endTime);
        this.sessionStartTime = null;
        this.setState({ pomSessionStop: false, pomAssociationProject: "", sessionEnd: endTime, timeLeft: 1500000, startPom: false });
    };
    onPomSessionLoggingCancel = () => {
        this.setState({ pomAssociationProject: "", pomSessionStop: false, timeLeft: 1500000 });
    };
    onPomAlertSelection = (event) => {
        console.log(event.target.value);
        this.setState({ pomAssociationProject: event.target.value });
    };


    render() {
        const { openProjectModal } = this.props;
        return (
            <div className="userDashboard">
                <Grid
                    container
                    spacing={0}
                    direction="column"
                    alignItems="center"
                    justify="center"
                    style={{ minHeight: '30vh' }}
                >
                    <Paper elevation={0} style={{ width: "50%", textAlign: "center", marginBottom: "10%" }} >
                        <Typography color="primary" variant="h4" style={{ fontWeight: "bold" }}>Session</Typography>
                        <CardContent style={{ textAlign: "center", justifyContent: "center", padding: "0" }}>
                            {/* <h1>{this.state.minutes + ":" + this.state.seconds + ":" + this.state.milliseconds}</h1> */}
                            <h1>{this.state.timeLeft}</h1>
                        </CardContent>
                        <CardActions style={{ justifyContent: "center" }}>
                            <div>
                                <Button onClick={() => this.checkStartStop()} size="medium" variant={this.state.startPom ? "outlined" : "contained"} color="secondary" fullWidth >{this.state.startPom ? "Pause" : "Start"}</Button>
                            </div>
                            <div>
                                <Button onClick={() => this.resetTimer()} size="medium" variant="contained" color="secondary" fullWidth>Stop</Button>
                            </div>
                        </CardActions>
                    </Paper>
                </Grid>

                <div style={this.isMobileDevice() ? { display: "none" } : { justifyContent: "right", textAlign: "right", marginRight: "10%" }}>
                    <Button onClick={() => openProjectModal()} size="small" variant="contained" color="primary">+ Project</Button>
                </div>
                <ProjectModal />
                <div style={this.isMobileDevice() ? { textAlign: "center", justifyContent: "center", marginLeft: "10%", marginRight: "10%" } : { textAlign: "center", justifyContent: "center", marginLeft: "10%", marginRight: "10%", marginBottom: "30%" }}>
                    <Paper elevation={0} className="userTable" style={{ width: "100%" }}>
                        <TableContainer className="tableContainer">
                            <Table aria-label="table">
                                <TableHead>
                                    <TableRow>
                                        <TableCell align="center">Project Id</TableCell>
                                        <TableCell align="center">Project Name</TableCell>
                                        <TableCell align="center">Completed Pomodoros</TableCell>
                                        <TableCell align="center">Last Session Completed</TableCell>
                                        <TableCell align="center"></TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {this.state.tableData
                                        .map(row => {
                                            return (
                                                <TableRow hover key={row.id}>
                                                    <TableCell component="th" scope="row" align="center">{row.id}</TableCell>
                                                    <TableCell align="center">{row.projectname}</TableCell>
                                                    <TableCell align="center">1</TableCell>
                                                    <TableCell align="center">2020-04-08T20:00Z</TableCell>
                                                    <TableCell algin="center">
                                                        <Button size="small" color="primary" onClick={() => this.onEdit(row.id)}>edit</Button>
                                                        <Button size="small" color="secondary" onClick={() => this.onDelete(row.id)}>delete</Button>
                                                    </TableCell>
                                                </TableRow>
                                            );
                                        })}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Paper>
                </div>
                <div style={this.isMobileDevice() ? { justifyContent: "center", textAlign: "center", marginBottom: "30%", marginTop: "8%" } : { display: "none" }}>
                    <Fab
                        color="primary"
                        size="large"
                        aria-label="add"
                        onClick={() => openProjectModal()}
                    >
                        <AddIcon />
                    </Fab>
                </div>
                {/* <div style={this.isMobileDevice() ? { justifyContent: "center", textAlign: "center", marginBottom: "30%", marginTop: "3%" } : { justifyContent: "center", textAlign: "center", marginBottom: "30%", marginTop: "3%" }}>
                    <Button onClick={() => openProjectModal()} size="small" variant="contained" color="primary">+ Project</Button>
                </div> */}
                {/* DIALOG - Associating a Pomodoro to a project */}
                <Dialog
                    open={this.state.associatePomAlert}
                    onClose={this.onPomAssociateCancel}
                    aria-labelledby="form-dialog-title"
                    style={{ justifyContent: "center", textAlign: "center", marginBottom: "2%" }}
                >
                    <DialogTitle id="alert-dialog-title">{"Pomodoro Project Association"}</DialogTitle>
                    <DialogContent>
                        <DialogContentText id="alert-dialog-description">
                            Which project would you like to associate this Pomodoro with?
                        </DialogContentText>
                        <FormControl variant="outlined" style={{ width: "90%", marginTop: "3%", marginBottom: "6%" }}>
                            <InputLabel id="demo-simple-select-outlined-label">Projects</InputLabel>
                            <Select
                                labelId="demo-simple-select-outlined-label"
                                id="demo-simple-select-outlined"
                                value={this.state.pomAssociationProject}
                                onChange={this.onPomAlertSelection}
                                label="Project"
                                input={<Input />}
                            >
                                <MenuItem value="None">No Project Association</MenuItem>
                                {
                                    this.state.tableData.map(project => {
                                        return (
                                            <MenuItem key={project.id} value={project.projectname}>Project - {project.projectname}</MenuItem>
                                        );
                                    })
                                }
                            </Select>
                        </FormControl>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.onPomAssociateCancel} color="primary">
                            Cancel
                        </Button>
                        <Button onClick={this.onPomAssociateAgree} color="secondary" autoFocus>
                            Go
                        </Button>
                    </DialogActions>
                </Dialog>
                {/* DIALOG - Quitting a Pomodoro Session */}
                <Dialog
                    open={this.state.pomSessionStop}
                    onClose={this.onPomSessionLoggingCancel}
                    aria-labelledby="form-dialog-title"
                    style={{ justifyContent: "center", textAlign: "center", marginBottom: "2%" }}
                >
                    <DialogTitle id="alert-dialog-title">{"Stopping Pomodoro Session"}</DialogTitle>
                    <DialogContent>
                        <DialogContentText id="alert-dialog-description">
                            Do you want to log the partial Pomodoro time to the project - {this.state.pomAssociationProject}?
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.onPomSessionLoggingCancel} color="primary">
                            No
                        </Button>
                        <Button onClick={this.onPomSessionLoggingAgree} color="secondary" autoFocus>
                            Yes
                        </Button>
                    </DialogActions>
                </Dialog>
                {/* DIALOG - Deleting a Project */}
                <Dialog
                    open={this.state.deleteAlert}
                    onClose={this.onDeleteAlertCancel}
                    aria-labelledby="form-dialog-title"
                >
                    <DialogTitle id="alert-dialog-title">{"Are You Sure You Want to Delete?"}</DialogTitle>
                    <DialogContent>
                        <DialogContentText id="alert-dialog-description">
                            Deleting a project is permanent and can't be undone
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.onDeleteAlertCancel} color="primary">
                            Disagree
                        </Button>
                        <Button onClick={this.onDeleteAlertAgreement} color="primary" autoFocus>
                            Agree
                        </Button>
                    </DialogActions>
                </Dialog>
            </div >
        );
    }
}

const mapStateToProps = ({ projectModal, loginPage }) => {
    return { projectModal, loginPage };
};
const mapDispatchToProps = {
    openProjectModal: actionCreators.openProjectModal,
    closeProjectModal: actionCreators.closeProjectModal,
    editProjectModal: actionCreators.editProjectModal,
    deleteProjectModal: actionCreators.deleteProjectModal
};
export default connect(mapStateToProps, mapDispatchToProps)(UserDashboard);;
