import React, { Component } from 'react';
import '../styles/userReport.scss';


import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
// import FormHelperText from '@material-ui/core/FormHelperText';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import Input from '@material-ui/core/Input';
import FormGroup from '@material-ui/core/FormGroup';

import { connect } from 'react-redux';


import axios from 'axios';

class UserReport extends Component {
    constructor(props) {
        super(props);
        this.state = {
            userId: this.props.userId,
            projectData: [],
            selectedProject: "",
            reportData: []
        };
    }
    isMobileDevice() {
        return (navigator.userAgent.includes("Android") || navigator.userAgent.includes("iPhone"));
    }
    componentDidMount() {
        let url = window.location.href.split("/");
        const userID = url[url.length - 2];
        axios.get(process.env.REACT_APP_API_URL + `/users/${userID}/projects`)
            .then(res => {
                this.setState({ projectData: res.data });
            })
            .catch(e => console.log(e));
    }
    onProjectSelection = (event) => {
        console.log(event.target.value);
        this.setState({ selectedProject: event.target.value });
    };
    onSubmit = () => {
        const projID = this.state.selectedProject.split("-")[0];
        console.log(projID);
        // axios.get(process.env.REACT_APP_API_URL + `/users/${userID}/projects/${projID}`)
        //     .then(res => {
        //         this.setState({ reportData: res.data });
        //     })
        //     .catch(e => console.log(e));
    };
    render() {
        return (
            <div className="report" style={{ marginTop: "5%" }}>
                <Typography variant={this.isMobileDevice() ? "h6" : "h5"} style={{ textAlign: "center", marginBottom: "5%" }}>User Report Console</Typography>
                <FormGroup style={{ justifyContent: "center", textAlign: "center", alignItems: "center" }}>
                    <h4>Report Form</h4>
                    {/* <TextField error={this.state.fnError} required name="firstName" value={this.state.firstName} onChange={this.handleChange} variant="outlined" autoFocus margin="dense" label="FirstName" style={{ width: '80%' }} />
                    <TextField error={this.state.lnError} required name="lastName" value={this.state.lastName} onChange={this.handleChange} variant="outlined" autoFocus margin="dense" label="LastName" style={{ width: '80%' }} /> */}
                    <FormControl variant="outlined" style={this.isMobileDevice() ? { width: "80%" } : { width: "30%" }}>
                        <InputLabel id="demo-simple-select-outlined-label">Project</InputLabel>
                        <Select
                            labelId="demo-simple-select-outlined-label"
                            id="demo-simple-select-outlined"
                            value={this.state.selectedProject}
                            onChange={this.onProjectSelection}
                            label="Project"
                            input={<Input />}
                        >
                            <MenuItem value="None">None</MenuItem>
                            {
                                this.state.projectData.map(project => {
                                    return (
                                        <MenuItem key={project.id} value={project.id + "-" + project.projectname}>{project.id + "-" + project.projectname}</MenuItem>
                                    );
                                })
                            }
                        </Select>
                    </FormControl>
                    {/* <FormControl variant="outlined" style={{ width: "80%" }}>
                        <InputLabel id="demo-simple-select-outlined-label">Sessions</InputLabel>
                        <Select
                            labelId="demo-simple-select-outlined-label"
                            id="demo-simple-select-outlined"
                            value={this.state.selectedProject}
                            onChange={this.onProjectSelection}
                            label="Project"
                            input={<Input />}
                        >
                            <MenuItem value="All">All</MenuItem>
                            {
                                this.state.projectData.map(project => {
                                    return (
                                        <MenuItem key={project.id} value={project.projectname}>Project - {project.projectname}</MenuItem>
                                    );
                                })
                            }
                        </Select>
                    </FormControl> */}
                    <div style={{ margin: "5%", }}>
                        <Button onClick={this.onSubmit} autoFocus variant="contained" color="secondary">Get Report</Button>
                    </div>
                </FormGroup>
            </div>
        );
    }
}


const mapStateToProps = ({ loginPage }) => {
    return loginPage;
};

export default connect(mapStateToProps)(UserReport);