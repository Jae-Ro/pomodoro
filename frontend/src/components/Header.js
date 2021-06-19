import React, { Component } from 'react';
// import { makeStyles } from '@material-ui/core/styles';
import Toolbar from '@material-ui/core/Toolbar';
// import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Avatar from '@material-ui/core/Avatar';
import MenuItem from '@material-ui/core/MenuItem';
import Popover from '@material-ui/core/Popover';
import { NavLink } from "react-router-dom";
import logo from '../assets/tomato.svg';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
// import ListItemText from '@material-ui/core/ListItemText';

import Divider from '@material-ui/core/Divider';
import Drawer from '@material-ui/core/Drawer';





import { Redirect } from 'react-router-dom';

import '../styles/header.scss';
import { connect } from 'react-redux';

import axios from 'axios';

class Header extends Component {
    constructor(props) {
        super(props);
        this.state = {
            signout: false,
            clickSignout: false,
            userId: this.props.userId,
            menuOpen: false,
            anchorEl: null,
            top: false,
            left: false,
            bottom: false,
            right: false,
        };
    }

    isMobileDevice() {
        return (navigator.userAgent.includes("Android") || navigator.userAgent.includes("iPhone"));
    }
    isAdminPage() {
        const currUrl = window.location.href;
        if (currUrl.includes("admin")) return true;
        else return false;
    }
    getUserById(userID) {
        axios.get(process.env.REACT_APP_API_URL + `/users/${userID}`)
            .then(res => {
                console.log(res);
                this.setState({
                    fnInitial: res.data.firstName.charAt(0).toUpperCase(),
                    lnInitial: res.data.lastName.charAt(0).toUpperCase(),
                    firstName: res.data.firstName.charAt(0).toUpperCase() + res.data.firstName.slice(1),
                    lastName: res.data.lastName.charAt(0).toUpperCase() + res.data.lastName.slice(1),
                    userId: userID
                });
            })
            .catch(e => console.log(e));
    }
    componentDidMount() {
        let url = window.location.href.split("/");
        let userID;
        if (url.includes("report")) {
            userID = url[url.length - 2];
            this.getUserById(userID);
        } else if (this.isAdminPage()) {
            this.setState({ firstName: "admin", lastName: "admin" });
        } else {
            userID = (this.state.userId) ? this.state.userId : this.props.state.userId;
            this.getUserById(userID);
        }
        return { firstName: this.state.userFN, lastName: this.state.userLN };
    }
    alertSignout = () => {
        this.setState({ signout: true, clickSignout: false });
    };
    onSignOut = () => {
        // this.setState({ signout: true });
        this.setState({ clickSignout: true });
    };
    handleClose = () => {
        this.setState({ clickSignout: false });
    };
    handleAvatarClick = (event) => {
        // console.log(event.currentTarget);
        this.setState({ anchorEl: event.currentTarget });
    };
    handleMenuClose = () => {
        this.setState({ anchorEl: null });
    };
    toggleDrawer = (side, open) => {
        this.setState({ left: open });
    };
    sideList = (side) => (
        <div
            className="sideList"
            role="presentation"
            onClick={() => this.toggleDrawer(side, false)}
            onKeyDown={() => this.toggleDrawer(side, false)}
            style={{ margin: '15px' }}
        >
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', marginBottom: '2px' }} >
                <div style={{ width: '100%' }}>
                    <h4 style={{ fontWeight: 'bold', textAlign: 'center', color: "#FF4A35" }}>Menu</h4>
                </div>
            </div>
            <Divider />
            <List className="toggle-drawer" style={{ alignItems: 'center' }}>
                <ListItem button>
                    <NavLink to={this.isAdminPage() ? `/ptt/admin` : `/ptt/users/${this.state.userId}`}>Home</NavLink>
                </ListItem>
                <ListItem button>
                    <NavLink to={this.isAdminPage() ? `/ptt/admin` : `/ptt/users/${this.state.userId}`}>About</NavLink>
                </ListItem>
                <ListItem button>
                    <NavLink to={this.isAdminPage() ? `/ptt/admin/report` : `/ptt/users/${this.state.userId}/report`}>Report</NavLink>
                </ListItem>

            </List>
        </div>
    );
    render() {
        const { signout } = this.state;
        if (signout) return <Redirect to="/" />;
        return (
            <div className="header">
                <div className="inner-header">
                    <Toolbar style={this.isMobileDevice() ? { height: "80px" } : { margin: "0.5% 2% 2%", height: "80px" }}>
                        <IconButton
                            edge="start"
                            className="menuButton"
                            color="inherit" aria-label="menu"
                            style={this.isMobileDevice() ? { marginRight: "theme.spacing(2)", display: "flex" } : { display: "none" }}
                            onClick={() => this.toggleDrawer("left", true)}
                        >
                            <MenuIcon />
                        </IconButton>
                        <div className="logo" style={this.isMobileDevice() ? { textAlign: "center", marginLeft: "5%" } : {}}>
                            <img alt="pomodoro-logo" src={logo} style={{ verticalAlign: "bottom" }} height="35px" />
                            <NavLink to={this.isAdminPage() ? `/ptt/admin` : `/ptt/users/${this.state.userId}`} >Pomodoro</NavLink>
                            {/* <Typography variant={this.isMobileDevice() ? "h6" : "h5"} className="headerTitle" style={{ flexGrow: "1" }}>POMODORO</Typography> */}
                        </div>

                        <div className="navigation" style={this.isMobileDevice() ? { display: "none" } : { display: "flex" }}>
                            <NavLink to={this.isAdminPage() ? `/ptt/admin` : `/ptt/users/${this.state.userId}`}>Home</NavLink>
                            <NavLink to={this.isAdminPage() ? `/ptt/admin` : `/ptt/users/${this.state.userId}`}>About</NavLink>
                            <NavLink to={this.isAdminPage() ? `/ptt/admin/report` : `/ptt/users/${this.state.userId}/report`}>Report</NavLink>
                        </div>
                        <Button onClick={this.handleAvatarClick}>
                            {/* <Avatar>{String(this.state.fnInitial + this.state.lnInitial)}</Avatar> */}
                            <Avatar src="/broken-image.jpg" />
                        </Button>
                        <Popover
                            open={Boolean(this.state.anchorEl)}
                            anchorEl={this.state.anchorEl}
                            onClose={this.handleMenuClose}
                            anchorOrigin={{
                                vertical: 'bottom',
                                horizontal: 'center',
                            }}
                            transformOrigin={{
                                vertical: 'top',
                                horizontal: 'center',
                            }}>
                            <MenuItem>Profile</MenuItem>
                            <MenuItem>Settings</MenuItem>
                            <MenuItem style={{ color: "red" }} onClick={this.onSignOut}>Sign Out</MenuItem>
                        </Popover>
                    </Toolbar>
                    <Dialog
                        open={this.state.clickSignout}
                        onClose={this.handleClose}
                        aria-labelledby="form-dialog-title"
                    >
                        <DialogTitle id="alert-dialog-title">{"Ready to Log out?"}</DialogTitle>
                        <DialogContent>
                            <DialogContentText id="alert-dialog-description">
                                Once you log out, you'll have to log back in
                        </DialogContentText>
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={this.handleClose} color="primary">
                                Cancel
                        </Button>
                            <Button onClick={this.alertSignout} color="secondary" autoFocus>
                                Signout
                        </Button>
                        </DialogActions>
                    </Dialog>
                    <Drawer anchor="left" open={this.state.left} onClose={() => this.toggleDrawer('left', false)} >
                        {this.sideList('left')}
                    </Drawer>
                </div>
            </div >
        );
    }
}

const mapStateToProps = ({ loginPage }) => {
    return loginPage;
};
export default connect(mapStateToProps)(Header);

;
