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

import AddIcon from '@material-ui/icons/Add';
import Fab from '@material-ui/core/Fab';

import UserModal from '../components/UserModal';
import { connect } from 'react-redux';
import axios from 'axios';
// import Link from '@material-ui/core/Link';



import '../styles/adminDashboard.scss';
import { actionCreators } from '../models/actions/userModalActions';

class AdminDashboard extends Component {
    constructor(props) {
        super(props);
        this.state = {
            tableData: [],
            deleteAlert: false,
            deleteId: 0
        };
    }
    isMobileDevice() {
        return (navigator.userAgent.includes("Android") || navigator.userAgent.includes("iPhone"));
    }
    componentDidMount() {
        axios.get(process.env.REACT_APP_API_URL + "/users")
            .then(res => {
                console.log(res);
                this.setState({ tableData: res.data });
            })
            .catch(e => console.log(e));
    }
    componentDidUpdate(prevProps, prevState) {
        setTimeout(() => {
            if ((prevProps.userModalOpen === true && this.props.userModalOpen === false) || (prevState.deleteAlert === true && this.state.deleteAlert === false)) {
                // console.log('getting data now');
                axios.get(process.env.REACT_APP_API_URL + "/users")
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
        let foundUser = null;
        for (let user of this.state.tableData) {
            if (user.id === id) {
                foundUser = user;
                // console.log("FOUND IT: ", foundUser);
            }
        }
        this.props.editUserModal(foundUser);
    };
    deleteRequest(id) {
        axios.delete(process.env.REACT_APP_API_URL + `/users/${id}`)
            .then(resp => {
                console.log(resp);
            })
            .catch(e => console.log(e));
    }
    onDelete(id) {
        let foundId = null;
        for (let user of this.state.tableData) {
            if (user.id === id) {
                foundId = id;
                // console.log("FOUND IT: ", foundId);
            }
        }
        this.setState({ deleteAlert: true, deleteId: foundId });
    };
    onDeleteAlertAgreement = () => {
        this.deleteRequest(this.state.deleteId);
        this.props.deleteUserModal(this.state.deleteId);
        this.setState({ deleteAlert: false, deleteId: 0 });

    };
    onDeleteAlertCancel = () => {
        this.setState({ deleteAlert: false, deleteId: 0 });
    };
    render() {
        // const { userModalOpen, submitUserModal, userTableData, editUserModal } = this.props
        const { openUserModal } = this.props;
        return (
            <div className="adminDashboard" style={{ marginTop: "5%" }}>
                <Typography variant={this.isMobileDevice() ? "h6" : "h5"} style={{ textAlign: "center", marginBottom: "5%" }}>Admin Console</Typography>
                <div style={this.isMobileDevice() ? { display: "none" } : { justifyContent: "right", textAlign: "right", marginRight: "10%" }}>
                    <Button onClick={() => openUserModal()} size="small" variant="contained" color="primary">+ User</Button>
                </div>
                <UserModal />
                <div style={{ textAlign: "center", justifyContent: "center", marginLeft: "3%", marginRight: "3%" }}>
                    <Paper elevation={0} className="userTable" style={{ width: "100%" }}>
                        <TableContainer className="tableContainer" style={{ maxHeight: "50vh" }}>
                            <Table aria-label="sticky table">
                                <TableHead>
                                    <TableRow>
                                        <TableCell align="center">User Id</TableCell>
                                        <TableCell align="center">First Name</TableCell>
                                        <TableCell align="center">Last Name</TableCell>
                                        <TableCell align="center">Email</TableCell>
                                        <TableCell align="center"></TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {this.state.tableData
                                        .map(row => {
                                            return (
                                                <TableRow hover key={row.id}>
                                                    <TableCell component="th" scope="row" align="center">{row.id}</TableCell>
                                                    <TableCell align="center">{row.firstName}</TableCell>
                                                    <TableCell align="center">{row.lastName}</TableCell>
                                                    <TableCell align="center">{row.email}</TableCell>
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
                        onClick={() => openUserModal()}
                    >
                        <AddIcon />
                    </Fab>
                </div>
                <Dialog
                    open={this.state.deleteAlert}
                    onClose={this.onDeleteAlertCancel}
                    aria-labelledby="form-dialog-title"
                >
                    <DialogTitle id="alert-dialog-title">{"Are You Sure You Want to Delete?"}</DialogTitle>
                    <DialogContent>
                        <DialogContentText id="alert-dialog-description">
                            Deleting a user is permanent and can't be undone
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
            </div>
        );
    }
}

const mapStateToProps = ({ userModal }) => {
    return userModal;
};
const mapDispatchToProps = {
    openUserModal: actionCreators.openUserModal,
    closeUserModal: actionCreators.closeUserModal,
    editUserModal: actionCreators.editUserModal,
    deleteUserModal: actionCreators.deleteUserModal
};
export default connect(mapStateToProps, mapDispatchToProps)(AdminDashboard);;
