import React, { Component } from 'react';
// import { connect } from 'react-redux';

import '../styles/login.scss';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
// import CardHeader from '@material-ui/core/CardHeader';
// import CardMedia from '@material-ui/core/CardMedia';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import FormGroup from '@material-ui/core/FormGroup';
// import Toolbar from '@material-ui/core/Toolbar';
// import Link from '@material-ui/core/Link';
import axios from 'axios';

import { Redirect } from 'react-router-dom';
import { connect } from 'react-redux';
import { loginActionCreators } from '../models/actions/loginPageActions';
import logo from '../assets/tomato.svg';


class Login extends Component {
    constructor(props) {
        super(props);
        this.state = {
            email: "",
            password: "",
            emailError: false,
            passwordError: false,
            adminValid: false,
            userValid: false,
            userId: ""
        };
    }
    isMobileDevice() {
        return (navigator.userAgent.includes("Android") || navigator.userAgent.includes("iPhone"));
    }
    handleChange = ({ target }) => {
        this.setState({ [target.name]: target.value });
    };
    onEnter(event) {
        if (event.key === 'Enter') this.validateLogin();
    }
    validateLogin = () => {
        if (this.state.email !== "") {
            let email = this.state.email.toLowerCase().trim();
            // let password = this.state.password.toLowerCase().trim();
            if (email.includes("admin")) {
                this.setState({ adminValid: true });
            } else {
                axios.get(process.env.REACT_APP_API_URL + '/users')
                    .then(res => {
                        for (let user of res.data) {
                            if (user.email === this.state.email) {
                                this.props.submitLogin(user.id);
                                this.setState({ userValid: true, userId: user.id });
                            }
                        }
                    })
                    .catch(e => console.log(e));
            }
        } else {
            this.setState({ emailError: true });
        }

        this.props.history.push("/");
    };
    render() {
        const { adminValid, userValid, userId } = this.state;
        if (adminValid === true) return <Redirect to="/ptt/admin" />;
        else if (userValid === true) return <Redirect to={`/ptt/users/${userId}`} />;

        return (
            <div style={{ backgroundColor: "#F5F5F5" }}>
                {/* <Toolbar style={this.isMobileDevice() ? { height: "80px", justifyContent: "center" } : { height: "80px" }}>
                    <Typography variant="h5" className="loginHeader" style={{ marginLeft: "2%" }}>Pomodoro Time Tracker</Typography>
                </Toolbar> */}
                <Grid
                    container
                    spacing={0}
                    direction="column"
                    alignItems="center"
                    justify="space-evenly"
                    style={{ minHeight: '100vh', backgroundColor: "#F5F5F5" }}
                >
                    <Card style={this.isMobileDevice() ? { width: "75%", justifyContent: "center", textAlign: "center", alignItems: "center" } : { width: "30%", justifyContent: "center", textAlign: "center", alignItems: "center" }}>
                        <div className="logo" style={{ textAlign: "center", marginTop: "10%" }}>
                            <img alt="pomodoro-logo" src={logo} style={{ verticalAlign: "bottom" }} height="35px" />
                            <h3 style={{ color: "#F8624E" }}>Pomodoro</h3>
                        </div>
                        <Typography color="primary" variant="h4" style={{ fontWeight: "bold" }}>Login</Typography>
                        <Typography>Please Login Below</Typography>
                        <CardContent style={{ textAlign: "center", justifyContent: "center" }}>
                            <FormGroup >
                                <div style={{ justifyContent: "center", textAlign: "center" }}>
                                    <TextField
                                        error={this.state.emailError}
                                        onChange={this.handleChange}
                                        required
                                        name="email"
                                        variant="outlined"
                                        autoFocus margin="dense"
                                        label="Email"
                                        value={this.state.email}
                                        placeholder="ex.gburdell@gatech.edu"
                                        style={{ width: "85%" }}
                                        onKeyPress={(e) => this.onEnter(e)}
                                        color="primary"
                                    />
                                </div>
                            </FormGroup>
                        </CardContent>
                        <CardActions style={{ justifyContent: "center", marginBottom: "15%" }}>
                            <div style={{ width: "50%" }}>
                                <Button onClick={this.validateLogin} size="medium" variant="contained" color="secondary" fullWidth >Login</Button>
                            </div>
                        </CardActions>
                    </Card>
                </Grid>
            </div >
        );
    }
}

const mapStateToProps = ({ loginPage }) => {
    return loginPage;
};
const mapDispatchToProps = {
    submitLogin: loginActionCreators.submitLogin
};
export default connect(mapStateToProps, mapDispatchToProps)(Login);
